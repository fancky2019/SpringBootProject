CREATE DATABASE `demo11` /*!40100 DEFAULT CHARACTER
SET utf8mb4 COLLATE utf8mb4_unicode_ci *//*!80016 DEFAULT ENCRYPTION = 'N' */

CREATE TABLE `demo_product` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `guid` VARCHAR(36) DEFAULT NULL,
  `product_name` VARCHAR(255) DEFAULT NULL,
  `product_style` VARCHAR(255) DEFAULT NULL,
  `image_path` VARCHAR(500) DEFAULT NULL,
  `create_time` DATETIME(3) DEFAULT NULL,
  `modify_time` DATETIME(3) DEFAULT NULL,
  `status` TINYINT DEFAULT NULL,
  `description` VARCHAR(1000) DEFAULT NULL,
  `timestamp` TIMESTAMP NULL DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=INNODB AUTO_INCREMENT=550001 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `person` (
                          `id` bigint NOT NULL AUTO_INCREMENT,
                          `name` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
                          `age` int DEFAULT NULL,
                          `birthday` timestamp NULL DEFAULT NULL,
                          PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;