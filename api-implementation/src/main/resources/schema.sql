-- MySQL Script generated by MySQL Workbench
-- Sun Nov 14 02:31:52 2021
-- Model: New Model    Version: 1.0
-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS = @@UNIQUE_CHECKS, UNIQUE_CHECKS = 0;
SET @OLD_FOREIGN_KEY_CHECKS = @@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS = 0;
SET @OLD_SQL_MODE = @@SQL_MODE, SQL_MODE =
        'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema mydb
-- -----------------------------------------------------
-- -----------------------------------------------------
-- Schema tasksapidb
-- -----------------------------------------------------
DROP SCHEMA IF EXISTS `tasksapidb`;

-- -----------------------------------------------------
-- Schema tasksapidb
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `tasksapidb` DEFAULT CHARACTER SET utf8;
USE `tasksapidb`;

-- -----------------------------------------------------
-- Table `tasksapidb`.`groups`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `tasksapidb`.`groups`;

CREATE TABLE IF NOT EXISTS `tasksapidb`.`groups`
(
    `name`            VARCHAR(50)  NOT NULL,
    `description`     VARCHAR(250) NULL DEFAULT NULL,
    `applicationUser` VARCHAR(250) NOT NULL,
    `createdAt`       TIMESTAMP    NULL DEFAULT CURRENT_TIMESTAMP(),
    `updatedAt`       TIMESTAMP    NULL DEFAULT CURRENT_TIMESTAMP() ON UPDATE CURRENT_TIMESTAMP(),
    PRIMARY KEY (`name`)
)
    ENGINE = InnoDB
    DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `tasksapidb`.`users`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `tasksapidb`.`users`;

CREATE TABLE IF NOT EXISTS `tasksapidb`.`users`
(
    `username`        VARCHAR(100) NOT NULL,
    `email`           VARCHAR(320) NULL,
    `applicationUser` VARCHAR(250) NOT NULL,
    `createdAt`       TIMESTAMP    NULL DEFAULT CURRENT_TIMESTAMP(),
    `updatedAt`       TIMESTAMP    NULL DEFAULT CURRENT_TIMESTAMP() ON UPDATE CURRENT_TIMESTAMP(),
    PRIMARY KEY (`username`),
    UNIQUE INDEX `email_UNIQUE` (`email` ASC)
)
    ENGINE = InnoDB
    DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `tasksapidb`.`projects`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `tasksapidb`.`projects`;

CREATE TABLE IF NOT EXISTS `tasksapidb`.`projects`
(
    `id`               BIGINT(20)   NOT NULL AUTO_INCREMENT,
    `identifier`       VARCHAR(250) NOT NULL,
    `name`             VARCHAR(250) NOT NULL,
    `description`      VARCHAR(500) NULL DEFAULT NULL,
    `createdAt`        TIMESTAMP    NULL DEFAULT CURRENT_TIMESTAMP(),
    `updatedAt`        TIMESTAMP    NULL DEFAULT CURRENT_TIMESTAMP() ON UPDATE CURRENT_TIMESTAMP(),
    `applicationUser`  VARCHAR(250) NOT NULL COMMENT 'This column specifies the application name that used the API',
    `users_username`   VARCHAR(100) NOT NULL,
    `prefixIdentifier` VARCHAR(45)  NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE INDEX `identifier_UNIQUE` (`identifier` ASC),
    INDEX `fk_tasks_users1_idx` (`users_username` ASC),
    UNIQUE INDEX `prefixIdentifier_UNIQUE` (`prefixIdentifier` ASC),
    CONSTRAINT `fk_tasks_users10`
        FOREIGN KEY (`users_username`)
            REFERENCES `tasksapidb`.`users` (`username`)
            ON DELETE NO ACTION
            ON UPDATE NO ACTION
)
    ENGINE = InnoDB
    AUTO_INCREMENT = 4
    DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `tasksapidb`.`tasks`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `tasksapidb`.`tasks`;

CREATE TABLE IF NOT EXISTS `tasksapidb`.`tasks`
(
    `id`              BIGINT(20)   NOT NULL AUTO_INCREMENT,
    `identifier`      VARCHAR(250) NOT NULL,
    `name`            VARCHAR(250) NOT NULL,
    `description`     VARCHAR(500) NULL DEFAULT NULL,
    `status`          VARCHAR(100) NULL DEFAULT NULL,
    `createdAt`       TIMESTAMP    NULL DEFAULT CURRENT_TIMESTAMP(),
    `updatedAt`       TIMESTAMP    NULL DEFAULT CURRENT_TIMESTAMP() ON UPDATE CURRENT_TIMESTAMP(),
    `applicationUser` VARCHAR(250) NOT NULL COMMENT 'This column specifies the application name that used the API',
    `dueDate`         DATETIME     NULL DEFAULT NULL,
    `users_username`  VARCHAR(100) NOT NULL,
    `projects_id`     BIGINT(20)   NOT NULL,
    PRIMARY KEY (`id`, `projects_id`),
    UNIQUE INDEX `identifier_UNIQUE` (`identifier` ASC),
    INDEX `fk_tasks_users1_idx` (`users_username` ASC),
    INDEX `fk_tasks_projects1_idx` (`projects_id` ASC),
    CONSTRAINT `fk_tasks_users1`
        FOREIGN KEY (`users_username`)
            REFERENCES `tasksapidb`.`users` (`username`)
            ON DELETE NO ACTION
            ON UPDATE NO ACTION,
    CONSTRAINT `fk_tasks_projects1`
        FOREIGN KEY (`projects_id`)
            REFERENCES `tasksapidb`.`projects` (`id`)
            ON DELETE NO ACTION
            ON UPDATE NO ACTION
)
    ENGINE = InnoDB
    AUTO_INCREMENT = 4
    DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `tasksapidb`.`assigns`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `tasksapidb`.`assigns`;

CREATE TABLE IF NOT EXISTS `tasksapidb`.`assigns`
(
    `id`              BIGINT(20)   NOT NULL AUTO_INCREMENT,
    `identifier`      VARCHAR(36)  NOT NULL,
    `groups_name`     VARCHAR(50)  NULL DEFAULT NULL,
    `tasks_id`        BIGINT(20)   NOT NULL,
    `users_username`  VARCHAR(100) NULL DEFAULT NULL,
    `assignDate`      TIMESTAMP    NULL DEFAULT CURRENT_TIMESTAMP(),
    `applicationUser` VARCHAR(250) NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE INDEX `identifier_UNIQUE` (`identifier` ASC),
    INDEX `fk_Assigns_groups1_idx` (`groups_name` ASC),
    INDEX `fk_Assigns_tasks1_idx` (`tasks_id` ASC),
    INDEX `fk_Assigns_users1_idx` (`users_username` ASC),
    UNIQUE INDEX `tasks_id_users_username_UNIQUE` (`users_username` ASC, `tasks_id` ASC),
    UNIQUE INDEX `tasks_id_groups_name_UNIQUE` (`tasks_id` ASC, `groups_name` ASC),
    CONSTRAINT `fk_Assigns_groups1`
        FOREIGN KEY (`groups_name`)
            REFERENCES `tasksapidb`.`groups` (`name`)
            ON DELETE SET NULL
            ON UPDATE CASCADE,
    CONSTRAINT `fk_Assigns_tasks1`
        FOREIGN KEY (`tasks_id`)
            REFERENCES `tasksapidb`.`tasks` (`id`)
            ON DELETE CASCADE
            ON UPDATE CASCADE,
    CONSTRAINT `fk_Assigns_users1`
        FOREIGN KEY (`users_username`)
            REFERENCES `tasksapidb`.`users` (`username`)
            ON DELETE SET NULL
            ON UPDATE CASCADE
)
    ENGINE = InnoDB
    AUTO_INCREMENT = 2
    DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `tasksapidb`.`comments`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `tasksapidb`.`comments`;

CREATE TABLE IF NOT EXISTS `tasksapidb`.`comments`
(
    `id`              BIGINT(20)   NOT NULL AUTO_INCREMENT,
    `users_username`  VARCHAR(100) NOT NULL,
    `tasks_id`        BIGINT(20)   NOT NULL,
    `identifier`      CHAR(36)     NOT NULL,
    `text`            TEXT         NULL DEFAULT NULL,
    `applicationUser` VARCHAR(100) NOT NULL,
    `createdAt`       TIMESTAMP    NULL DEFAULT CURRENT_TIMESTAMP(),
    `updatedAt`       TIMESTAMP    NULL DEFAULT CURRENT_TIMESTAMP() ON UPDATE CURRENT_TIMESTAMP(),
    PRIMARY KEY (`id`),
    UNIQUE INDEX `identifier_UNIQUE` (`identifier` ASC),
    UNIQUE INDEX `fks_UNIQUE` (`users_username` ASC, `tasks_id` ASC, `identifier` ASC),
    INDEX `fk_comments_users1_idx` (`users_username` ASC),
    INDEX `fk_comments_tasks1_idx` (`tasks_id` ASC),
    CONSTRAINT `fk_comments_tasks1`
        FOREIGN KEY (`tasks_id`)
            REFERENCES `tasksapidb`.`tasks` (`id`)
            ON DELETE CASCADE
            ON UPDATE CASCADE,
    CONSTRAINT `fk_comments_users1`
        FOREIGN KEY (`users_username`)
            REFERENCES `tasksapidb`.`users` (`username`)
            ON DELETE CASCADE
            ON UPDATE CASCADE
)
    ENGINE = InnoDB
    AUTO_INCREMENT = 2
    DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `tasksapidb`.`spectators`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `tasksapidb`.`spectators`;

CREATE TABLE IF NOT EXISTS `tasksapidb`.`spectators`
(
    `id`              BIGINT(20)   NOT NULL AUTO_INCREMENT,
    `identifier`      VARCHAR(36)  NOT NULL,
    `groups_name`     VARCHAR(50)  NULL DEFAULT NULL,
    `tasks_id`        BIGINT(20)   NOT NULL,
    `users_username`  VARCHAR(100) NULL DEFAULT NULL,
    `createdAt`       TIMESTAMP    NULL DEFAULT CURRENT_TIMESTAMP(),
    `applicationUser` VARCHAR(250) NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE INDEX `identifier_UNIQUE` (`identifier` ASC),
    INDEX `fk_Assigns_groups1_idx` (`groups_name` ASC),
    INDEX `fk_Assigns_tasks1_idx` (`tasks_id` ASC),
    INDEX `fk_Assigns_users1_idx` (`users_username` ASC),
    UNIQUE INDEX `tasks_id_users_username_UNIQUE` (`tasks_id` ASC, `users_username` ASC),
    UNIQUE INDEX `tasks_id_groups_name_UNIQUE` (`tasks_id` ASC, `groups_name` ASC),
    CONSTRAINT `fk_Assigns_groups10`
        FOREIGN KEY (`groups_name`)
            REFERENCES `tasksapidb`.`groups` (`name`)
            ON DELETE SET NULL
            ON UPDATE CASCADE,
    CONSTRAINT `fk_Assigns_tasks10`
        FOREIGN KEY (`tasks_id`)
            REFERENCES `tasksapidb`.`tasks` (`id`)
            ON DELETE CASCADE
            ON UPDATE CASCADE,
    CONSTRAINT `fk_Assigns_users10`
        FOREIGN KEY (`users_username`)
            REFERENCES `tasksapidb`.`users` (`username`)
            ON DELETE SET NULL
            ON UPDATE CASCADE
)
    ENGINE = InnoDB
    AUTO_INCREMENT = 2
    DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `tasksapidb`.`users_has_groups`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `tasksapidb`.`users_has_groups`;

CREATE TABLE IF NOT EXISTS `tasksapidb`.`users_has_groups`
(
    `users_username` VARCHAR(100) NOT NULL,
    `groups_name`    VARCHAR(50)  NOT NULL,
    PRIMARY KEY (`users_username`, `groups_name`),
    INDEX `fk_users_has_groups_groups1_idx` (`groups_name` ASC),
    INDEX `fk_users_has_groups_users_idx` (`users_username` ASC),
    CONSTRAINT `fk_users_has_groups_groups1`
        FOREIGN KEY (`groups_name`)
            REFERENCES `tasksapidb`.`groups` (`name`)
            ON DELETE CASCADE
            ON UPDATE CASCADE,
    CONSTRAINT `fk_users_has_groups_users`
        FOREIGN KEY (`users_username`)
            REFERENCES `tasksapidb`.`users` (`username`)
            ON DELETE CASCADE
            ON UPDATE CASCADE
)
    ENGINE = InnoDB
    DEFAULT CHARACTER SET = utf8;


SET SQL_MODE = @OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS = @OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS = @OLD_UNIQUE_CHECKS;
