-- phpMyAdmin SQL Dump
-- version 3.3.7deb7
-- http://www.phpmyadmin.net
--
-- Host: localhost
-- Generation Time: Feb 16, 2013 at 12:30 AM
-- Server version: 5.1.66
-- PHP Version: 5.3.3-7+squeeze14

SET SQL_MODE="NO_AUTO_VALUE_ON_ZERO";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Database: `mineandcraft`
--

-- --------------------------------------------------------

--
-- Table structure for table `mnc_bans`
--

CREATE TABLE IF NOT EXISTS `mnc_bans` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL,
  `expiration` bigint(20) NOT NULL,
  `canceled` int(1) NOT NULL DEFAULT '0',
  `reason` text,
  `banningone` varchar(32) DEFAULT NULL,
  `banned_date` datetime NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `mnc_chat_blocklist`
--

CREATE TABLE IF NOT EXISTS `mnc_chat_blocklist` (
  `user_id` int(11) NOT NULL,
  `blocked_id` int(11) NOT NULL,
  UNIQUE KEY `user_id` (`user_id`,`blocked_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `mnc_chests`
--

CREATE TABLE IF NOT EXISTS `mnc_chests` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `owner_id` int(11) NOT NULL,
  `world` varchar(32) NOT NULL,
  `x` int(11) NOT NULL,
  `y` int(11) NOT NULL,
  `z` int(11) NOT NULL,
  `type` varchar(32) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `COORDS` (`x`,`y`,`z`)
) ENGINE=MyISAM  DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `mnc_currency`
--

CREATE TABLE IF NOT EXISTS `mnc_currency` (
  `user_id` int(11) NOT NULL,
  `balance` bigint(20) NOT NULL,
  PRIMARY KEY (`user_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `mnc_ips`
--

CREATE TABLE IF NOT EXISTS `mnc_ips` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL,
  `ip_address` varchar(256) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `user_id` (`user_id`,`ip_address`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `mnc_ipwhitelist`
--

CREATE TABLE IF NOT EXISTS `mnc_ipwhitelist` (
  `user_id` int(11) NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`user_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `mnc_places`
--

CREATE TABLE IF NOT EXISTS `mnc_places` (
  `name` varchar(16) CHARACTER SET utf8 NOT NULL,
  `owner_id` int(11) NOT NULL,
  `type` enum('public','private','vip') NOT NULL DEFAULT 'public',
  `x` int(11) NOT NULL,
  `y` int(11) NOT NULL,
  `z` int(11) NOT NULL,
  `welcome_message` tinytext,
  `world` varchar(64) NOT NULL DEFAULT 'world',
  `num_id` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`name`),
  UNIQUE KEY `x` (`x`,`y`,`z`,`world`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `mnc_places_permissions`
--

CREATE TABLE IF NOT EXISTS `mnc_places_permissions` (
  `place_id` varchar(16) NOT NULL,
  `user_id` int(11) NOT NULL,
  UNIQUE KEY `unique_comb` (`place_id`,`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `mnc_profession`
--

CREATE TABLE IF NOT EXISTS `mnc_profession` (
  `user_id` int(11) NOT NULL,
  `experience` bigint(20) NOT NULL,
  PRIMARY KEY (`user_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `mnc_users`
--

CREATE TABLE IF NOT EXISTS `mnc_users` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(16) COLLATE utf8_bin NOT NULL,
  `username_clean` varchar(16) COLLATE utf8_bin NOT NULL,
  `password` varchar(42) COLLATE utf8_bin NOT NULL,
  `email` tinytext COLLATE utf8_bin NOT NULL,
  `join_date` datetime NOT NULL,
  `lastlogin` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  PRIMARY KEY (`id`),
  UNIQUE KEY `username_clean` (`username_clean`),
) ENGINE=MyISAM  DEFAULT CHARSET=utf8 COLLATE=utf8_bin AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `mnc_vip`
--

CREATE TABLE IF NOT EXISTS `mnc_vip` (
  `user_id` int(11) NOT NULL,
  `expiration` bigint(20) NOT NULL,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
