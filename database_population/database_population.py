import datetime
import json
import random

MENU_FILE = "menu_items.json"
INGREDIENTS_FILE = "ingredients.json"

TYPES = ["BOWL", "PLATE", "DOUBLE PLATE"]
PRICES = {"BOWL" : 8.99, "PLATE" : 9.99, "DOUBLE PLATE" : 11.99}

class MenuItem:
    def __init__(self, id, name, ingredients, add_cost, entree) -> None:
        self.id = id
        self.name = name
        self.ingredients = ingredients
        self.add_cost = add_cost
        self.entree = entree
    
    def __repr__(self) -> str:
        return f"Name: {self.name}, Ingredients: {len(self.ingredients)}, Additional Cost: {self.add_cost}, Entree: {self.entree}"

class Ingredient:
    def __init__(self, id, name, threshold, price, unit, stock) -> None:
        self.id = id
        self.name = name
        self.threshold = threshold
        self.price = price
        self.unit = unit
        self.stock = stock

    def __repr__(self) -> str:
        return f"Threshold: {self.threshold}, Price: {self.price}, Unit: {self.unit}, Stock: {self.stock}"

class Order:
    def __init__(self, id, server, price, order_type) -> None:
        self.id = id
        self.server = server
        self.price = price
        self.order_type = order_type
        self.timestamp = datetime.datetime.now()

    def __repr__(self) -> str:
        return f"Server: {self.server}, Price: {self.price}, Type: {self.order_type}, Timestamp: {self.timestamp}"
 
def load_data():

    """
    Just a helper function to load the data from the jsons into memory
    Also generates id's to write to the db for the ingredients and menu items

    returns menu_items: dict<MenuItem> and Ingredients: dict<Ingredients> 
    """

    with open(MENU_FILE, 'r') as mf:
        menu_items_data = json.load(mf)
    with open(INGREDIENTS_FILE, 'r') as inf:
        ingredients_data = json.load(inf)
    
    ingredients = {}
    ingredient_id = 1 
    for name, data in ingredients_data.items():
        ingredients[name] = Ingredient(ingredient_id, name, data['threshold'], data['price'], data['unit'], max(0, data['threshold'] + random.randint(-10, 10)))
        ingredient_id += 1

    menu_items = {}
    menu_item_id = 1 
    for item, data in menu_items_data.items():
        menu_items[item] = MenuItem(menu_item_id, item, data['Ingredients'], data['Additional Cost'], data['Entree'])
        menu_item_id += 1

    return menu_items, ingredients

def get_order_type():
    return random.choice(TYPES)

def create_random_order(order_id, menu_items):
    """
    Creates a random order given an order_id and menu_items (the dictionary from the load_data function)
    This will automatically add the additional cost of menu items to the cost
    Will assign a valid cashier id to the order

    returns the order object + list of entrees + side, you need these to populate the join table
    """

    order_type = get_order_type()
    entrees_count = 1 if order_type == 'BOWL' else 2 if order_type == 'PLATE' else 3
    entrees = random.sample([item for item in menu_items.values() if item.entree == 1], entrees_count)
    side = random.choice([item for item in menu_items.values() if item.entree == 0])

    total_price = PRICES[order_type]
    total_price += sum(entree.add_cost + menu_items[entree.name].add_cost for entree in entrees) + menu_items[side.name].add_cost

    server_id = random.randint(2, 7)

    order = Order(id=order_id, server=server_id, price=total_price, order_type=TYPES.index(order_type))

    return order, entrees, side

def write_menu_items_and_ingredients(menu_items, ingredients):
    """
    TODO
    Function to commit the menu items and ingredients as they are to the database
    Ideally only need to run this once (total)
    """

    pass

def write_order(order, entrees, side):
    """
    TODO
    Function to commit an order to the database
    Should also run this once
    """
    pass

def main():
    menu_items, ingredients = load_data()

    print(menu_items)
    print(ingredients)

    print("""
          
          #######################################
          
          """)

    orders = []

    for i in range(10):
        orders.append(create_random_order(i, menu_items))
    
    for order in orders:
        print(order)

if __name__ == "__main__":
    main()
