# LotTracing

A custom product manager for tracking and generating lot information throughout the product life cycle.

This application's purpose is to trace through the product manufacturing life cycle, tracking essential information along each step to maintain conformity to strict tolerances. Additionally, search functionality allows the user to view any record from virtually any piece of information associated with it.

The life cycle begins by receiving shipments of individual components from a vendor. Each component will be tracked by lot number and other information can be added along the way such as vendor information and component attributes. From there, final products are tracked using the individual components that make them up as well as final product lot numbers. Assembly dates and quantities are all tracked during the assembly process. Final product assemblies can then be added dynamically to fill purchase order requests all while tracking shipment and buyer details. Support for return shipments and multiple product assemblies to one purchase order is also available. Every aspect of the life cycle is completely searchable by almost any associated piece of information and is all stored in a completely custom MySQL database.

Some helpful features:

- A product development tool to create custom products that include special product information.

- Contact manager for Vendor and Buyer information including email ,phone, company, and notes.

- Active status for each product in the database to hide decomissioned or unavailable products without deleting them.
