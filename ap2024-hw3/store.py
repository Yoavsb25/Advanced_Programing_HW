import yaml
import errors
from item import Item
from shopping_cart import ShoppingCart


class Store:
    def __init__(self, path):
        with open(path) as inventory:
            items_raw = yaml.load(inventory, Loader=yaml.FullLoader)['items']
        self._items = self._convert_to_item_objects(items_raw)
        self._shopping_cart = ShoppingCart()

    @staticmethod
    def _convert_to_item_objects(items_raw):
        return [Item(item['name'],
                     int(item['price']),
                     item['hashtags'],
                     item['description'])
                for item in items_raw]

    def get_items(self) -> list:
        return self._items

    def search_by_name(self, item_name: str) -> list:
        # Creates a list of all items that has item_name in their name
        items = [item for item in self._items if item_name in item.name]
        # Sorts the list according to instructions
        return self.sorted_hashtags(items)

    def search_by_hashtag(self, hashtag: str) -> list:
        # Creates a list of all items that has hashtag in their hashtags list
        items = [item for item in self._items if hashtag in item.hashtags]
        # Sorts the list according to instructions
        return self.sorted_hashtags(items)

    def add_item(self, item_name: str):
        # Creates a list of all items that has item_name in their name
        matching_items = [item for item in self._items if item_name in item.name]
        num_matching_items = len(matching_items)

        # If no matches were found
        if num_matching_items == 0:
            raise errors.ItemNotExistError(f"Item '{item_name}' doesn't exist in the store.")
        # If to many matches were found
        elif num_matching_items > 1:
            raise errors.TooManyMatchesError(f"Too many items in the store match the name: '{item_name}' you provided.")
        # if the item found is already in the shopping cart
        elif matching_items[0] in self._shopping_cart.cart:
            raise errors.ItemAlreadyExistsError(f"Item '{item_name}' already exists in the shopping cart.")
        # Adds the only match to the shopping cart
        else:
            self._shopping_cart.add_item(matching_items[0])

    def remove_item(self, item_name: str):
        # Creates a list of all items in the shopping cart that has item_name in their name
        matching_items = [item for item in self._shopping_cart.cart if item_name in item.name]
        num_matching_items = len(matching_items)
        # If no matches were found
        if num_matching_items == 0:
            raise errors.ItemNotExistError(f"Item '{item_name}' isn't in your shopping cart.")
        # If to many matches were found
        elif num_matching_items > 1:
            raise errors.TooManyMatchesError(f"Too many items match the name: '{item_name}' you provided.")
        # Removes the only match from the shopping cart
        else:
            self._shopping_cart.remove_item(item_name)

    def checkout(self) -> int:
        # Returns the total price of all the items in the costumer’s shopping cart.
        return self._shopping_cart.get_subtotal()

    def sorted_hashtags(self, items_list: list):
        # Creates a list to store all hashtags in the current shopping cart
        shopping_cart_hashtags = []
        # Goes over all hashtags of every item
        for item in self._shopping_cart.cart:
            # adds all hashtags of every item to shopping_cart_hashtags
            for hashtag in item.hashtags:
                shopping_cart_hashtags.append(hashtag)

        items_list_hashtags = {}
        # Goes over all items from items_list
        for item in items_list:
            items_list_hashtags[item] = 0
            # counts how many hashtags item has in common with shopping_cart_hashtags
            for hashtag in item.hashtags:
                items_list_hashtags[item] += shopping_cart_hashtags.count(hashtag)

        # Sort items based on the sum of common hashtags and item names
        sorted_items = sorted(items_list_hashtags.items(), key=lambda product: (-product[1], product[0].name))
        # Creates a list of items from the sorted items, excluding those already in the shopping cart
        result = [item[0] for item in sorted_items if item[0] not in self._shopping_cart.cart]
        return result
