package LotNumbers;

import Commons.UIComponents.popUps.Notifier;
import Commons.UIComponents.popUps.componentDetailsPopUp;
import Commons.UIComponents.tableViews;
import Products.product;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXTreeTableView;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;

import java.util.ArrayList;
import java.util.Optional;


public class assemblyLotController
{

    private lotNumbersController parentController;
    private product assembledProduct;

    private static ObservableList<componentLot> componentLotList = FXCollections.observableArrayList();
    private static String nullCharacter = "--";

    @FXML
    JFXTreeTableView<componentLot> componentLotTable;

    @FXML
    JFXTextField qtyField;

    @FXML
    JFXDatePicker assemblyDatePicker;

    @FXML
    JFXTextArea memoField;

    @FXML
    Label productLabel;


    public void initialize()
    {
        initComponentLotTable();
        assembledProduct = null;
        updateLabel();

    }

    void setParentController(lotNumbersController parentController) {
        this.parentController = parentController;
    }


    private void updateLabel()
    {
        if(assembledProduct == null)
        {
            productLabel.setStyle("-fx-font-size: 16; -fx-text-fill: red");
            productLabel.setText("Product: None");
        }
        else
        {
            productLabel.setStyle("-fx-font-size: 16; -fx-text-fill: #06bc0c");
            productLabel.setText("Product: " + assembledProduct.partNumber.getValue());
        }
    }

    private void initComponentLotTable()
    {
        componentLotTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        //Enable better multiple selection of rows
        componentLotTable.setRowFactory(param -> {

            final TreeTableRow<componentLot> row = new TreeTableRow<>();

            row.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {

                final int index = row.getIndex();

                if(index >= 0 && index < componentLotTable.getCurrentItemsCount() &&
                        componentLotTable.getSelectionModel().isSelected(index)) {

                    componentLotTable.getSelectionModel().clearSelection(index);
                    event.consume();
                }
                else if(!componentLotTable.getSelectionModel().isSelected(index))
                {
                    componentLotTable.getSelectionModel().select(index);
                }
            });

            row.addEventFilter(MouseEvent.MOUSE_RELEASED, Event::consume);

            return row;
        });


        tableViews.initComponentLotTable(componentLotTable,componentLotList,nullCharacter);

        ContextMenu menu = new ContextMenu();
        MenuItem copyItem = new MenuItem("    View Details    ");

        menu.getItems().addAll(copyItem);
        componentLotTable.setContextMenu(menu);

        //Since we are allowing the user to select multiple components
        //at the same time we should only display component details if
        //we have a single component selected/ selectDetails() will handle this
        copyItem.setOnAction(e -> selectDetails());

    }

    private void selectDetails()
    {
        if(componentLotTable.getSelectionModel().getSelectedItems().size() == 1)
        {
            componentDetailsPopUp.getComponentDetails(componentLotTable.getSelectionModel().
                    getSelectedItem().getValue().componentPartNumber.get());
        }
        else
        {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Multiple Select Error");
            alert.setHeaderText("Multiple components selected");
            alert.setContentText("Please make sure only one component is selected to view details.");
            alert.show();
        }
    }

    //Because Back is called from LotNumbersController via a reference to this
    //controller we need to give it a method to call to clear out this page.
    //We cant use the 'goBack' method which is only for sending us back to the
    //main lot number page.
    public void clearAll()
    {
        if(componentLotTable != null)
        {
            componentLotTable.getSelectionModel().clearSelection();
        }

        if(assemblyDatePicker != null)
        {
            assemblyDatePicker.setValue(null);
        }

        if(qtyField != null)
        {
            qtyField.clear();
        }

        parentController.searchField.setVisible(false);

        assembledProduct = null;

        if(productLabel != null)
        {
            updateLabel();
        }

    }

    //move to the home tab (center) then hide
    //the table search field. Then change the
    //header label back to the home tab header
    public void goBack()
    {
        parentController.tabLayout.getSelectionModel().select(1);
        parentController.searchField.setVisible(false);
        parentController.headerLabel.setText("Lot Numbers");
        componentLotTable.getSelectionModel().getSelectedItems().clear();
        assembledProduct = null;
        updateLabel();
    }

    //This is called from the parent controller whenever we load 'Lot Numbers'
    void updateAssemblyTable()
    {
        //componentList is Observable so we can just update
        //the list and the UI will listen for the changes
        DAL.lotNumbersDAO.getComponentLotList(componentLotList);
    }

    //This method will check the assembled product, the assembly date,
    //and the assembled quantity fields and return a boolean to indicate
    //if they are valid. If any of the fields are not valid we output
    //a notification for each input
    private boolean sanitizeInputs()
    {
        boolean flag = true;

        if(assembledProduct == null)
        {
            Notifier.getErrorNotification("No Input Error","Please select the assembled product");

            flag = false;
        }

        if(assemblyDatePicker.getValue() == null)
        {
            Notifier.getErrorNotification("No Input Error","Please select an assembly date");

            flag = false;
        }

        if(qtyField.getText().isEmpty())
        {
            Notifier.getErrorNotification("No Input Error","Please select a quantity");

            flag = false;
        }
        else
        {
            String qty = qtyField.getText();

            if (!qty.matches("^[1-9]\\d*$"))
            {
                Notifier.getErrorNotification("Invalid Input Error","Please enter a valid quantity");

                flag = false;
            }
        }


        return flag;
    }

    //Assembly lot strings are formatted as follows
    //3 alphanumeric chars for the date using the convention implemented in the
    //utilities class in Commons.
    //the last 4 chars of the UPC.
    //2 letters representing the lot suffix
    //and 1 alphanumeric char that is unique
    private String getAssemblyLotNumber()
    {
        StringBuilder number = new StringBuilder();

        String key = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

        number.append(Commons.utilities.dateToCodeConversion(assemblyDatePicker.getValue()));
        number.append(assembledProduct.UPC.get().substring(assembledProduct.UPC.get().length()-4));
        number.append(assembledProduct.lotSuffix.get());

        ArrayList<String> uniqueArr = DAL.lotNumbersDAO.getSameDayAssemblyLots(number.toString());

        //Since the unique character at the end of the lot number is alphanumeric
        //there are a total of 62 possible characters so if we ever get a case where
        //there are 62 elements in the array that means there are no more unique characters
        //we can use for the assembly lot number. If this is the case we can return a string
        //'inval' since that could never be a valid string and parse it in the createLot() method
        if(uniqueArr.size() >= 62)
        {
            return "inval";
        }

        //Remove all of the existing unique codes from the key string
        for(int i=0; i<uniqueArr.size(); i++)
        {
            String curr = uniqueArr.get(i).substring(uniqueArr.get(i).length()-1);
            key = key.replace(curr,"");
        }

        //Now all we are left with is unused characters so we can just choose
        //the first character in the string. We are also guaranteed to have a
        //non-empty string since we know the uniqueArr has to have less than
        //62 elements and key.length() == 62
        number.append(key.charAt(0));

        return number.toString();

    }


    //This handles creating the actual assembly lot. To do
    //this we first need to sanitize all of the input, then
    //assemble the assembly lot number, and finally, add
    //the new assembly lot to the database. Additionally,
    //we have to add the associated component lots to
    //the lot bridge
    public void createLot()
    {
        boolean assemblyLotSuccess, bridgeSuccess = true;

        if(!sanitizeInputs())
        {
            return;
        }

        if(componentLotTable.getSelectionModel().getSelectedItems().isEmpty())
        {
            Alert conf = new Alert(Alert.AlertType.CONFIRMATION);
            conf.setTitle("Confirmation");
            conf.setHeaderText("There are no component lots selected");
            conf.setContentText("Do you want to create the assembly lot anyway?");

            ButtonType yes = new ButtonType("Yes");
            ButtonType  no = new ButtonType("No");


            conf.getButtonTypes().setAll(yes,no);
            Optional<ButtonType> result = conf.showAndWait();


            if(result.isPresent() && result.get() == no)
            {
                return;
            }

        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation");
        confirm.setHeaderText("Are you sure you want to create this lot?");

        //List all of the associated component lots that will be tagged to the new assembly lot
        StringBuilder confirmationContent = new StringBuilder("Assembled Product: ");
        confirmationContent.append(assembledProduct.partNumber.get());

        confirmationContent.append("\n\n\nComponent Lots Used:\n\n");

        for(int j=0; j<componentLotTable.getSelectionModel().getSelectedItems().size(); j++)
        {
            confirmationContent.append("Lot Number: ");
            confirmationContent.append(componentLotTable.getSelectionModel().getSelectedItems().get(j).getValue().ComponentLotNumber.get());
            confirmationContent.append("\t\tPart Number: ");
            confirmationContent.append(componentLotTable.getSelectionModel().getSelectedItems().get(j).getValue().componentPartNumber.get());
            confirmationContent.append("\n");
        }

        confirmationContent.append("\n\n");

        confirm.setContentText(confirmationContent.toString());

        ButtonType yes = new ButtonType("Yes");
        ButtonType  no = new ButtonType("No");


        confirm.getButtonTypes().setAll(yes,no);
        Optional<ButtonType> result = confirm.showAndWait();


        if(result.isPresent() && result.get() == no)
        {
            return;
        }

        //If we get here that means all inputs are valid

        //Now we need to assemble the new assembly lot object
        String assemblyLotNumber = getAssemblyLotNumber();

        //We need to check to see if we can generate the assembly lot
        if(assemblyLotNumber.compareTo("inval") == 0)
        {
            Notifier.getErrorNotification("Failed to generate lot number","There can only be 62 assembly\n" +
                    "of the same type in the same day");
            return;
        }

        assemblyLot newLot = new assemblyLot();

        newLot.AssemblyLotNumber = new SimpleStringProperty(assemblyLotNumber);
        newLot.assembleDate = assemblyDatePicker.getValue();
        newLot.assembleQty = new SimpleIntegerProperty(Integer.parseInt(qtyField.getText()));
        newLot.partNumber = new SimpleStringProperty(assembledProduct.partNumber.get());

        if(!memoField.getText().isEmpty())
        {
            newLot.memos = new SimpleStringProperty(memoField.getText());
        }

        //Once we get here we have a completed assemblyLot object

        //Now we need to store it in the database


        assemblyLotSuccess = DAL.lotNumbersDAO.insertAssemblyLot(newLot);

        if(!componentLotTable.getSelectionModel().getSelectedItems().isEmpty())
        {
            ArrayList<componentLot> componentLots = new ArrayList<>();


            for(int i=0; i<componentLotTable.getSelectionModel().getSelectedItems().size(); i++)
            {
                //Stupid java8 bug workaround
                if(componentLotTable.getSelectionModel().getSelectedItems().get(i) != null)
                {
                    componentLots.add(componentLotTable.getSelectionModel().getSelectedItems().get(i).getValue());
                }
            }

            bridgeSuccess = DAL.lotNumbersDAO.insertAssemblyComponentLots(newLot.AssemblyLotNumber.get(),componentLots);
        }



        if(assemblyLotSuccess && bridgeSuccess)
        {
            Notifier.getSuccessNotification("Success!","Added " + newLot.AssemblyLotNumber.get() + " to database");
        }
        else if(bridgeSuccess)
        {
            Notifier.getErrorNotification("Failed","Failed to add " + newLot.AssemblyLotNumber.get() + "to database");
        }
        else if(assemblyLotSuccess)
        {
            Notifier.getErrorNotification("Failed","Failed to add one or more lot numbers to the bridge");
        }
        else
        {
            Notifier.getErrorNotification("Failed","Failed to update database");
        }

        goBack();
    }

    public void selectProduct(product product)
    {
        assembledProduct = product;
        updateLabel();
    }

    public void getProduct()
    {
        productSelectorUI ui = new productSelectorUI();
        ui.createUI(this);

    }





}
