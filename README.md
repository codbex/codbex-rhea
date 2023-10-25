⚠️ To be migrated to Eclipse Dirigible 8.x ⚠️

# codbex-rhea
Modelling and Applications Generation Platform

### Best Practices

* Always have an `Id` property as primary key
* Always have a `Name` property, could be in some cases plays as a compond key
* Always choose a `type-safe` approach in modelling, e.g. having separate Customer and Supplier entities instead of a single Partner entity
* Use CamelUpperCase for perspective names (no spaces in the keys)
* Separate simplest as possible modules, e.g. Orders, Invoices, Payments
* Place Master-Details entities in a separate perspective
