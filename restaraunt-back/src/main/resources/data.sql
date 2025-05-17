-- Categories
INSERT IGNORE INTO category (id, name) VALUES
  (1, 'Starters'),
  (2, 'Mains'),
  (3, 'Desserts'),
  (4, 'Beverages'),
  (5, 'Sides'),
  (6, 'Specials');

-- Dishes
INSERT IGNORE INTO dish (id, name, description, price, category_id) VALUES
  (1, 'Bruschetta',          'Tomato & basil on toast',                       5.50, 1),
  (2, 'Caesar Salad',        'Romaine, croutons & Caesar dressing',           7.00, 1),
  (3, 'Steak',               'Grilled sirloin with peppercorn sauce',        18.00, 2),
  (4, 'Pasta',               'Penne in tomato sauce',                        12.50, 2),
  (5, 'Tiramisu',            'Coffee-flavored Italian dessert',               6.00, 3),
  (6, 'Garlic Bread',        'Toasted bread with garlic butter',              4.00, 1),
  (7, 'Soup of the Day',     'Chef’s choice, served with bread',              5.00, 1),
  (8, 'Stuffed Mushrooms',   'Mushrooms filled with cheese & herbs',          6.50, 1),
  (9, 'Grilled Chicken',     'Marinated chicken breast with potatoes',       15.00, 2),
  (10, 'Fish & Chips',       'Beer-battered cod with fries & tartar',        14.50, 2),
  (11, 'Vegan Burger',       'Plant-based patty with avocado',               13.00, 2),
  (12, 'Lamb Curry',         'Slow-cooked lamb in spicy tomato sauce',       16.50, 2),
  (13, 'Cheesecake',         'Classic New York–style with berry compote',     6.50, 3),
  (14, 'Gelato Trio',        'Three scoops: chocolate, vanilla, pistachio',   5.50, 3),
  (15, 'Espresso',           'Single shot of dark-roast espresso',            3.00, 4),
  (16, 'Cappuccino',         'Espresso with steamed milk foam',               4.00, 4),
  (17, 'Herbal Tea',         'Choice of chamomile, mint, or rooibos',         3.50, 4),
  (18, 'House Red Wine',     'Glass of our selected red blend',               7.00, 4),
  (19, 'French Fries',       'Crispy golden fries with sea salt',             4.00, 5),
  (20, 'Onion Rings',        'Beer-battered onion rings',                     5.00, 5),
  (21, 'Mixed Vegetables',   'Steamed seasonal veggies',                      4.50, 5),
  (22, 'Chef’s Stew',        'Hearty beef stew with root vegetables',        14.00, 6),
  (23, 'Catch of the Day',   'Market-fresh fish grilled with lemon butter',  17.00, 6),
  (24, 'BBQ Ribs',           'Slow-roasted pork ribs with house BBQ sauce',  18.50, 6);
