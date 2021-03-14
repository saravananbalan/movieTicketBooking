-- MySQL dump 10.13  Distrib 5.7.32, for Linux (x86_64)
--
-- Host: localhost    Database: ticketBooking
-- ------------------------------------------------------
-- Server version	5.7.32-0ubuntu0.18.04.1

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `booking_details`
--

DROP TABLE IF EXISTS `booking_details`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `booking_details` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` varchar(50) DEFAULT NULL,
  `movieid` varchar(50) DEFAULT NULL,
  `booked_seats` int(11) NOT NULL DEFAULT '0',
  `hall_id` varchar(40) DEFAULT NULL,
  `seat_numbers` varchar(150) DEFAULT NULL,
  `show_time` varchar(20) DEFAULT NULL,
  `show_id` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `booking_details`
--

LOCK TABLES `booking_details` WRITE;
/*!40000 ALTER TABLE `booking_details` DISABLE KEYS */;
/*!40000 ALTER TABLE `booking_details` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `dashboard_details`
--

DROP TABLE IF EXISTS `dashboard_details`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `dashboard_details` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `cinemahall` varchar(20) DEFAULT NULL,
  `shows` varchar(11) DEFAULT NULL,
  `movies` varchar(20) DEFAULT NULL,
  `no_of_seats` int(5) DEFAULT NULL,
  `available_seats` int(5) DEFAULT NULL,
  `booked_seats` int(5) DEFAULT NULL,
  `ticket_rate` int(10) DEFAULT NULL,
  `cgst` int(6) DEFAULT NULL,
  `sgst` int(6) DEFAULT NULL,
  `hall_id` varchar(40) DEFAULT NULL,
  `movie_id` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `dashboard_details`
--

LOCK TABLES `dashboard_details` WRITE;
/*!40000 ALTER TABLE `dashboard_details` DISABLE KEYS */;
/*!40000 ALTER TABLE `dashboard_details` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `show_seat_details`
--

DROP TABLE IF EXISTS `show_seat_details`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `show_seat_details` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `movieid` varchar(50) DEFAULT NULL,
  `hall_id` varchar(40) DEFAULT NULL,
  `seat_numbers` varchar(1000) DEFAULT NULL,
  `show_id` varchar(50) DEFAULT NULL,
  `show_time` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `show_seat_details`
--

LOCK TABLES `show_seat_details` WRITE;
/*!40000 ALTER TABLE `show_seat_details` DISABLE KEYS */;
/*!40000 ALTER TABLE `show_seat_details` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_profiles`
--

DROP TABLE IF EXISTS `user_profiles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_profiles` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(25) DEFAULT NULL,
  `password` varchar(100) DEFAULT NULL,
  `role` varchar(25) DEFAULT NULL,
  `mobile_number` varchar(15) DEFAULT NULL,
  `email_id` varchar(21) DEFAULT NULL,
  `userid` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_profiles`
--

LOCK TABLES `user_profiles` WRITE;
/*!40000 ALTER TABLE `user_profiles` DISABLE KEYS */;
INSERT INTO `user_profiles` VALUES (1,'saravanan','EjhG2tlZxJhtYBe7YbZY8w==','admin','9566413966','12345@gmail.com',NULL);
/*!40000 ALTER TABLE `user_profiles` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2021-03-14 15:21:42
