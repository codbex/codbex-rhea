⚠️ To be migrated to Eclipse Dirigible 8.x ⚠️

# codbex-rhea
Modelling and Applications Generation Platform

### Best Practices

* Always have an `Id` property as primary key
* Always have a `Name` property, could be in some cases plays as a compond key
* For `Document` types of entities e.g. Order, Invoice, etc. use UUID calculated property with `require("utils/uuid").random()` as a source snippet or `(await import("@dirigible/utils")).uuid.random()`
* Wherever needed use weak dependency via property `Reference` which holds the UUID of the referenced document
* Always prefer a `type-safe` approach in modelling, e.g. having separate Customer and Supplier entities instead of a single Partner entity
* Use CamelUpperCase for perspective names (no spaces in the keys)
* Place Master-Details entities in a separate perspective
* Use `Dropdown` widget for hard dependencies with `Id` and `Name` for `Dropdown key` and `Dropdown value` respectively
