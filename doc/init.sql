CREATE DATABASE umi_test_db
  DEFAULT CHARACTER SET utf8
  DEFAULT COLLATE utf8_general_ci;
-- 

--
INSERT INTO role (id, name) VALUES (1, 'ROLE_USER');
INSERT INTO role (id, name) VALUES (2, 'ROLE_ADMIN');

-- INSERT INTO users (username, password, account_expired, account_locked, credentials_expired, account_enabled)VALUES ('test', '$2a$10$niyP97nIp8dEOM5FYuWPWeH14SN/4uPRztBjceWnBn6VZeObGl5kG', 0, 0, 0, 1);
-- insert into user_role values(1,1);

--------
INSERT INTO oauth_client_details (client_id, resource_ids, client_secret, scope, authorized_grant_types, access_token_validity, refresh_token_validity, additional_information)
VALUES ('umi_client', 'umi_resources', 'top_secret', 'read,write', 'password,refresh_token', 604800, 604800, '{}');


INSERT INTO `users` (`id`, `account_expired`, `account_locked`, `credentials_expired`, `account_enabled`, `password`, `username`, deleted)
VALUES (1, FALSE, FALSE, FALSE, TRUE, '$2a$10$Rlo1j1WY7CwxEHwu0C0exOGmVe4D59o1DO6CPBjPi3npIRZi.ykBi', 'admin', FALSE);
INSERT INTO `user_role` (`user_id`, `role_id`) VALUES (1, 1);

INSERT INTO `system_users` (id,username,password,account_enabled,account_expired,account_locked,credentials_expired) VALUES ('1', 'admin','$2a$10$aE1JwVQ0AKZq3..FZcY/n.VmNvZ.CjEGOIf5YLD4VlABln.KkU.iy',true,false,FALSE ,FALSE);
INSERT INTO `system_user_role` VALUES ('1', '2');

DROP TABLE IF EXISTS `topic_tip_off`;
CREATE TABLE `topic_tip_off` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `creation_time` datetime DEFAULT NULL,
  `deleted` bit(1) DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `reason` varchar(500) DEFAULT NULL,
  `topic_id` bigint(20) DEFAULT NULL,
  `user_id` bigint(20) DEFAULT NULL,
  `enabled` bit(1) NOT NULL,
  `done` bit(1) NOT NULL,
  `done_str` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK93cylckgideapeevoj871syp0` (`topic_id`),
  KEY `FKddvkpiqcms86w1rtjocw63f8w` (`user_id`),
  CONSTRAINT `FK93cylckgideapeevoj871syp0` FOREIGN KEY (`topic_id`) REFERENCES `topics` (`id`),
  CONSTRAINT `FKddvkpiqcms86w1rtjocw63f8w` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;


CREATE TABLE `user_tip_off` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `creation_time` datetime DEFAULT NULL,
  `deleted` bit(1) DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `done` bit(1) NOT NULL,
  `done_str` varchar(255) DEFAULT NULL,
  `reason` varchar(500) DEFAULT NULL,
  `tip_user_id` bigint(20) DEFAULT NULL,
  `user_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKm9lct8j5prnt49y2igxueotqc` (`tip_user_id`),
  KEY `FK77naxet81u69m28ljq7x7vxp5` (`user_id`),
  CONSTRAINT `FK77naxet81u69m28ljq7x7vxp5` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
  CONSTRAINT `FKm9lct8j5prnt49y2igxueotqc` FOREIGN KEY (`tip_user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;