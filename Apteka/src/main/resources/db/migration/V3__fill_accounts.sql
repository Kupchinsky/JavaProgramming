-- Delete test data
SET REFERENTIAL_INTEGRITY FALSE;

TRUNCATE TABLE `users`;
TRUNCATE TABLE `resources_users`;
TRUNCATE TABLE `resources`;

SET REFERENTIAL_INTEGRITY TRUE;

-- Put new data
-- admin: 123
-- trader: 123
INSERT INTO `users` (`id`, `login`, `passwordHash`, `salt`, `personName`) VALUES
	(1, 'admin', '56de704185aefb35d7f7c41f84edb96bbc878f7d', 'salt', 'Admin Adminov'),
	(2, 'trader', '56de704185aefb35d7f7c41f84edb96bbc878f7d', 'salt', 'Trader Traderov');

-- admin (users: admin)
--  recipes (view: traider)
--  products (view: traider)
--  sells (view: traider)
-- trader (users: admin, traider)
--  sell
--  create_recipe
INSERT INTO `resources` (`id`, `name`, `parent_resource_id`) VALUES
	(1, 'admin', NULL),
	(2, 'admin/recipes', 1),
	(3, 'admin/products', 1),
	(4, 'admin/sells', 1),

	(5, 'trader', NULL),
	(6, 'trader/sell', 5),
	(7, 'trader/create_recipe', 5);

INSERT INTO `resources_users` (`id`, `resource_id`, `user_id`, `role`) VALUES
	-- user: admin, resource: admin, role: CEO
	(1, 1, 1, 1),

	-- user: admin, resource: trader, role: TRADER
	(2, 5, 1, 2),

	-- user: trader, resource: admin, role: TRADER
	(3, 1, 2, 2),

	-- user: trader, resource: trader, role: TRADER
	(4, 5, 2, 2);
