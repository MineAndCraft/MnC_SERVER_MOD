-- ---------------------------------------------
-- Table structure for MnC server mod beta 1.0.0
-- ---------------------------------------------

SET SQL_MODE="NO_AUTO_VALUE_ON_ZERO";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Table structure for table `mnc_bans`
--

CREATE TABLE IF NOT EXISTS `mnc_bans` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL,
  `expiration` bigint(20) NOT NULL,
  `canceled` int(1) NOT NULL DEFAULT '0',
  `reason` text COLLATE utf8_bin,
  `banningone` varchar(32) COLLATE utf8_bin DEFAULT NULL,
  `banned_date` datetime NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM  DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- --------------------------------------------------------

--
-- Table structure for table `mnc_chat_blocklist`
--

CREATE TABLE IF NOT EXISTS `mnc_chat_blocklist` (
  `user_id` int(11) NOT NULL,
  `blocked_id` int(11) NOT NULL,
  UNIQUE KEY `blocker_blocked` (`user_id`,`blocked_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- --------------------------------------------------------

--
-- Table structure for table `mnc_chests`
--

CREATE TABLE IF NOT EXISTS `mnc_chests` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `owner_id` int(11) NOT NULL,
  `world` varchar(32) COLLATE utf8_bin NOT NULL,
  `x` int(11) NOT NULL,
  `y` int(11) NOT NULL,
  `z` int(11) NOT NULL,
  `type` varchar(32) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `coords` (`x`,`y`,`z`)
) ENGINE=MyISAM  DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- --------------------------------------------------------

--
-- Table structure for table `mnc_currency`
--

CREATE TABLE IF NOT EXISTS `mnc_currency` (
  `user_id` int(11) NOT NULL,
  `balance` bigint(20) NOT NULL,
  PRIMARY KEY (`user_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- --------------------------------------------------------

--
-- Table structure for table `mnc_ips`
--

CREATE TABLE IF NOT EXISTS `mnc_ips` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL,
  `ip_address` varchar(256) COLLATE utf8_bin NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `user_ip` (`user_id`,`ip_address`)
) ENGINE=MyISAM  DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- --------------------------------------------------------

--
-- Table structure for table `mnc_ipwhitelist`
--

CREATE TABLE IF NOT EXISTS `mnc_ipwhitelist` (
  `user_id` int(11) NOT NULL,
  PRIMARY KEY (`user_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- --------------------------------------------------------

--
-- Table structure for table `mnc_places`
--

CREATE TABLE IF NOT EXISTS `mnc_places` (
  `name` varchar(16) CHARACTER SET latin1 NOT NULL,
  `owner_id` int(11) NOT NULL,
  `type` enum('public','private','vip') COLLATE utf8_bin NOT NULL DEFAULT 'public',
  `x` int(11) NOT NULL,
  `y` int(11) NOT NULL,
  `z` int(11) NOT NULL,
  `welcome_message` tinytext COLLATE utf8_bin,
  `world` varchar(64) COLLATE utf8_bin NOT NULL DEFAULT 'world',
  `num_id` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`name`),
  UNIQUE KEY `coordinates` (`x`,`y`,`z`,`world`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- --------------------------------------------------------

--
-- Table structure for table `mnc_places_permissions`
--

CREATE TABLE IF NOT EXISTS `mnc_places_permissions` (
  `place_id` varchar(16) COLLATE utf8_bin NOT NULL,
  `user_id` int(11) NOT NULL,
  UNIQUE KEY `place_user` (`place_id`,`user_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- --------------------------------------------------------

--
-- Table structure for table `mnc_playermetadata`
--

CREATE TABLE IF NOT EXISTS `mnc_playermetadata` (
  `user_id` int(11) NOT NULL,
  `available_residence_blocks` int(11) NOT NULL DEFAULT '2000',
  PRIMARY KEY (`user_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- --------------------------------------------------------

--
-- Table structure for table `mnc_profession`
--

CREATE TABLE IF NOT EXISTS `mnc_profession` (
  `user_id` int(11) NOT NULL,
  `experience` bigint(20) NOT NULL,
  PRIMARY KEY (`user_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- --------------------------------------------------------

--
-- Table structure for table `mnc_residences`
--

CREATE TABLE IF NOT EXISTS `mnc_residences` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `owner_id` int(11) NOT NULL,
  `x1` int(11) NOT NULL,
  `x2` int(11) NOT NULL,
  `z1` int(11) NOT NULL,
  `z2` int(11) NOT NULL,
  `name` varchar(32) COLLATE utf8_bin NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`),
  KEY `x1` (`x1`),
  KEY `x2` (`x2`),
  KEY `z1` (`z1`),
  KEY `z2` (`z2`)
) ENGINE=MyISAM  DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- --------------------------------------------------------

--
-- Table structure for table `mnc_residences_accesses`
--

CREATE TABLE IF NOT EXISTS `mnc_residences_accesses` (
  `residence_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  UNIQUE KEY `residence_id` (`residence_id`,`user_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- --------------------------------------------------------

--
-- Table structure for table `mnc_users`
--

CREATE TABLE IF NOT EXISTS `mnc_users` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `username` varchar(16) COLLATE utf8_bin NOT NULL,
  `username_clean` varchar(16) COLLATE utf8_bin NOT NULL,
  `password` varchar(42) COLLATE utf8_bin NOT NULL,
  `email` tinytext COLLATE utf8_bin NOT NULL,
  `join_date` datetime NOT NULL,
  `lastlogin` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  PRIMARY KEY (`id`),
  UNIQUE KEY `username_clean` (`username_clean`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- --------------------------------------------------------

--
-- Table structure for table `mnc_vip`
--

CREATE TABLE IF NOT EXISTS `mnc_vip` (
  `user_id` int(11) NOT NULL,
  `expiration` bigint(20) NOT NULL,
  PRIMARY KEY (`user_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
