
CREATE TABLE `climbs` (
  `climb_id` int(11) NOT NULL,
  `gym_rating` int(11) DEFAULT NULL,
  `user_rating` int(11) DEFAULT NULL,
  `comments` text,
  `wall_id` int(11) DEFAULT NULL,
  `tape_color` text,
  `climb_type` text,
  PRIMARY KEY (`climb_id`)
)

CREATE TABLE `comments` (
  `comment_id` int(11) NOT NULL AUTO_INCREMENT,
  `comment_text` varchar(80) DEFAULT NULL,
  `user_name` varchar(20) DEFAULT NULL,
  `date_long` bigint(20) DEFAULT NULL,
  `climb_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`comment_id`)
)

CREATE TABLE `completed_climbs` (
  `user_name` varchar(15) NOT NULL,
  `climb_id` int(11) NOT NULL DEFAULT '0',
  `date_long` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`user_name`,`climb_id`)
)

CREATE TABLE `projects` (
  `user_name` varchar(15) NOT NULL,
  `climb_id` int(11) NOT NULL DEFAULT '0',
  `date_long` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`user_name`,`climb_id`)
)

CREATE TABLE `ratings` (
  `rating_id` int(11) NOT NULL AUTO_INCREMENT,
  `climb_id` int(11) DEFAULT NULL,
  `user_name` varchar(20) DEFAULT NULL,
  `user_rating` int(11) DEFAULT NULL,
  `user_like` tinyint(1) DEFAULT NULL,
  `user_dislike` tinyint(1) DEFAULT NULL,
  PRIMARY KEY (`rating_id`)
)

CREATE TABLE `user_information` (
  `user_name` varchar(20) NOT NULL,
  `name` text,
  `height` int(11) DEFAULT NULL,
  `skill_level` int(11) DEFAULT NULL,
  PRIMARY KEY (`user_name`)
)

CREATE TABLE `wall_sections` (
  `id` int(11) NOT NULL,
  `name` text,
  `top_out` tinyint(1) DEFAULT NULL,
  `wall_segment` int(11) DEFAULT NULL,
  `date_last_updated` date DEFAULT NULL,
  PRIMARY KEY (`id`)
)

CREATE TABLE `walls` (
  `id` int(11) NOT NULL,
  `name` text,
  PRIMARY KEY (`id`)
)