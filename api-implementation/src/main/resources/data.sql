/* Test Data */
INSERT INTO `tasksapidb`.`users` (`username`, `email`, `applicationUser`) VALUES ('RafailTest', 'RafailTest@gmail.com', 'TaskApplicationUI');
INSERT INTO `tasksapidb`.`groups` (`name`, `applicationUser`) VALUES ('RafailGroup', 'TaskApplicationUI');
INSERT INTO `tasksapidb`.`users_has_groups` (`users_username`, `groups_name`) VALUES ('RafailTest', 'RafailGroup');
INSERT INTO `tasksapidb`.`assigns` (`identifier`, `groups_name`, `tasks_id`, `users_username`, `applicationUser`) VALUES ('958afee0-ec71-11eb-9a03-0242ac130003', 'RafailGroup', '1', 'RafailTest', 'TaskApplicationUI');
INSERT INTO `tasksapidb`.`spectators` (`identifier`, `groups_name`, `tasks_id`, `users_username`, `applicationUser`) VALUES ('958b0d68-ec71-11eb-9a03-0242ac130003', 'RafailGroup', '1', 'RafailTest', 'TaskApplicationUI');
INSERT INTO `tasksapidb`.`tasks` (`identifier`, `name`, `description`, `status`, `applicationUser`, `users_username`) VALUES ('4848a045-378e-42ed-a1ea-31de6820ed74', 'create the database', 'In this task the team need to create the schema of the database.', 'active', 'TaskApplicationUI', 'RafailTest');


