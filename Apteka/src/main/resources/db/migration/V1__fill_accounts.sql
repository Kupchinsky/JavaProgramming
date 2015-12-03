-- Put new data
-- admin: 123
-- trader: 123
INSERT ALL
	INTO users (id, login, passwordHash, salt, personName) VALUES (1, 'admin', '56de704185aefb35d7f7c41f84edb96bbc878f7d', 'salt', 'Admin Adminov')
	INTO users (id, login, passwordHash, salt, personName) VALUES (2, 'trader', '56de704185aefb35d7f7c41f84edb96bbc878f7d', 'salt', 'Trader Traderov')
SELECT * FROM dual;

-- admin (users: admin)
--  recipes (view: traider)
--  products (view: traider)
--  sells (view: traider)
-- trader (users: admin, traider)
--  sell
--  create_recipe
INSERT ALL
	INTO resources (id, name, parent_resource_id) VALUES (1, 'admin', NULL)
	INTO resources (id, name, parent_resource_id) VALUES (2, 'admin/recipes', 1)
	INTO resources (id, name, parent_resource_id) VALUES (3, 'admin/products', 1)
	INTO resources (id, name, parent_resource_id) VALUES (4, 'admin/sells', 1)

	INTO resources (id, name, parent_resource_id) VALUES (5, 'trader', NULL)
	INTO resources (id, name, parent_resource_id) VALUES (6, 'trader/sell', 5)
	INTO resources (id, name, parent_resource_id) VALUES (7, 'trader/create_recipe', 5)
SELECT * FROM dual;

INSERT ALL
	-- user: admin, resource: admin, role: CEO
	INTO resources_users (resource_id, user_id, role) VALUES (1, 1, 1)
	INTO resources_users (resource_id, user_id, role) VALUES (2, 1, 1)
	INTO resources_users (resource_id, user_id, role) VALUES (3, 1, 1)
	INTO resources_users (resource_id, user_id, role) VALUES (4, 1, 1)

	-- user: admin, resource: trader, role: CEO
	INTO resources_users (resource_id, user_id, role) VALUES (5, 1, 1)
	INTO resources_users (resource_id, user_id, role) VALUES (6, 1, 1)
	INTO resources_users (resource_id, user_id, role) VALUES (7, 1, 1)

	-- user: admin, resource: trader, role: TRADER
	INTO resources_users (resource_id, user_id, role) VALUES (5, 1, 2)
	INTO resources_users (resource_id, user_id, role) VALUES (6, 1, 2)
	INTO resources_users (resource_id, user_id, role) VALUES (7, 1, 2)

	-- user: trader, resource: admin, role: TRADER
	INTO resources_users (resource_id, user_id, role) VALUES (1, 2, 2)
	INTO resources_users (resource_id, user_id, role) VALUES (2, 2, 2)
	INTO resources_users (resource_id, user_id, role) VALUES (4, 2, 2)

	-- user: trader, resource: trader, role: TRADER
	INTO resources_users (resource_id, user_id, role) VALUES (5, 2, 2)
	INTO resources_users (resource_id, user_id, role) VALUES (6, 2, 2)
	INTO resources_users (resource_id, user_id, role) VALUES (7, 2, 2)
SELECT * FROM dual;
