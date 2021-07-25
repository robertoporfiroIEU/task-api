DELETE FROM `tasksapidb`.`users` WHERE username = "RafailTest";
DELETE FROM `tasksapidb`.`users_has_groups` WHERE users_username = "RafailTest";
DELETE FROM `tasksapidb`.`groups` WHERE username = "RafailGroup";
DELETE FROM `tasksapidb`.`assigns` WHERE identifier = "958afee0-ec71-11eb-9a03-0242ac130003";
DELETE FROM `tasksapidb`.`spectators` WHERE identifier = "958b0d68-ec71-11eb-9a03-0242ac130003";
DELETE FROM `tasksapidb`.`tasks` WHERE identifier = "4848a045-378e-42ed-a1ea-31de6820ed74";

