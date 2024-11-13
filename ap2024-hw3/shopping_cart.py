import errors
from item import Item


class ShoppingCart:
    def __init__(self):
        self.cart = []

    def add_item(self, item: Item):
        # Check if the item is not already in the shopping cart
        if item not in self.cart:
            # If not in the cart, add the item to the shopping cart
            self.cart.append(item)
        else:
            # If the item is already in the cart, raise an error
            raise errors.ItemAlreadyExistsError(f"Item '{item.name}' already exists in the shopping cart.")

    def remove_item(self, item_name: str):
        # Check if there is an item with the same name
        for item in self.cart:
            # If found removes it
            if item.name == item_name:
                self.cart.remove(item)
                return
        # No items with the name were found
        raise errors.ItemNotExistError(f"Item '{item_name}' doesnt exists in the shopping cart.")

    def get_subtotal(self) -> int:
        # Sums up a list of all items in the shopping cart
        cart_price = sum([item.price for item in self.cart])
        return cart_price
