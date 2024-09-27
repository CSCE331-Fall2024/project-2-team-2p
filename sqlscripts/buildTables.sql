CREATE TABLE Ingredients (
    id INTEGER PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    stock INT NOT NULL,
    threshold INT NOT NULL,
    price FLOAT NOT NULL,
    unit INT NOT NULL  -- Enum for {GRAM, PIECE, ML, etc.}
);
-- Fill ingredients table
\i 'CSCE331/project2/scripts/addIngredients.sql'

CREATE TABLE MenuItems (
    id INTEGER PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    price FLOAT NOT NULL
);
-- Fill menuitems table
\i 'CSCE331/project2/scripts/addMenuItems.sql'

CREATE TABLE Employees (
    id INTEGER PRIMARY KEY,
    username VARCHAR(255) NOT NULL,
    pin INT NOT NULL,
    manager BOOLEAN NOT NULL
);
-- Fill employees table
\i 'CSCE331/project2/scripts/addEmployees.sql'

CREATE TABLE IngredientsMenuItems (
    id INTEGER PRIMARY KEY,
    IngredientKey INT NOT NULL,
    MenuItemKey INT NOT NULL,
    quantity INT NOT NULL,  -- How many units of the ingredient are needed
    FOREIGN KEY (IngredientKey) REFERENCES Ingredients(id),
    FOREIGN KEY (MenuItemKey) REFERENCES MenuItems(id)
);
-- Fill ingredientsmenuitems table
\i 'CSCE331/project2/scripts/addIngredientsMenuItems.sql'

CREATE TABLE Orders (
    id INTEGER PRIMARY KEY,
    server INT NOT NULL,
    price FLOAT NOT NULL,
    type INT NOT NULL,  -- Enum for {BOWL, PLATE, D PLATE, ...}
    timestamp DATE NOT NULL,
    FOREIGN KEY (server) REFERENCES Employees(id)
);

CREATE TABLE MenuItemsOrders (
    id INTEGER PRIMARY KEY,
    MenuItemKey INT NOT NULL,
    OrderKey INT NOT NULL,
    FOREIGN KEY (MenuItemKey) REFERENCES MenuItems(id),
    FOREIGN KEY (OrderKey) REFERENCES Orders(id)
);