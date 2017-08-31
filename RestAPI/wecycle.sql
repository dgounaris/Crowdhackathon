-- MySQL dump 10.13  Distrib 5.7.17, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: wecycledb
-- ------------------------------------------------------
-- Server version	5.7.19-log

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
-- Table structure for table `bank`
--

DROP TABLE IF EXISTS `bank`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `bank` (
  `BankId` bigint(40) NOT NULL AUTO_INCREMENT,
  `Bank_Name` varchar(45) NOT NULL,
  `Bank_Address` varchar(45) NOT NULL,
  PRIMARY KEY (`BankId`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `bank`
--

LOCK TABLES `bank` WRITE;
/*!40000 ALTER TABLE `bank` DISABLE KEYS */;
INSERT INTO `bank` VALUES (1,'Pireos','Leoforos Kifisias 137'),(2,'Eurobank','Odos Pleiadon');
/*!40000 ALTER TABLE `bank` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `bank_has_prepaid`
--

DROP TABLE IF EXISTS `bank_has_prepaid`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `bank_has_prepaid` (
  `Bank_BankId` bigint(40) NOT NULL,
  `Prepaid_Prepaid_Id` bigint(40) NOT NULL,
  PRIMARY KEY (`Bank_BankId`,`Prepaid_Prepaid_Id`),
  KEY `fk_Bank_has_Prepaid_Prepaid1_idx` (`Prepaid_Prepaid_Id`),
  KEY `fk_Bank_has_Prepaid_Bank1_idx` (`Bank_BankId`),
  CONSTRAINT `fk_Bank_has_Prepaid_Bank1` FOREIGN KEY (`Bank_BankId`) REFERENCES `bank` (`BankId`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_Bank_has_Prepaid_Prepaid1` FOREIGN KEY (`Prepaid_Prepaid_Id`) REFERENCES `prepaid` (`Prepaid_Id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `bank_has_prepaid`
--

LOCK TABLES `bank_has_prepaid` WRITE;
/*!40000 ALTER TABLE `bank_has_prepaid` DISABLE KEYS */;
/*!40000 ALTER TABLE `bank_has_prepaid` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `bins`
--

DROP TABLE IF EXISTS `bins`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `bins` (
  `id` bigint(40) NOT NULL AUTO_INCREMENT,
  `Lat` double NOT NULL,
  `Long` double NOT NULL,
  `Space` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `bins`
--

LOCK TABLES `bins` WRITE;
/*!40000 ALTER TABLE `bins` DISABLE KEYS */;
INSERT INTO `bins` VALUES (1,37.97945,23.71622,1),(2,37.930524,23.747831,1),(3,37.949654,23.74097,1),(4,37.942986,23.646983,1),(5,37.974215,23.784107,1),(6,37.995986,23.681108,1),(7,37.980383,23.732884,1),(8,37.965609,23.567709,1),(9,38.141707,23.858689,1),(10,37.929079,23.513327,1);
/*!40000 ALTER TABLE `bins` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `city`
--

DROP TABLE IF EXISTS `city`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `city` (
  `City_Id` bigint(40) NOT NULL AUTO_INCREMENT,
  `City_Name` varchar(45) NOT NULL,
  PRIMARY KEY (`City_Id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `city`
--

LOCK TABLES `city` WRITE;
/*!40000 ALTER TABLE `city` DISABLE KEYS */;
INSERT INTO `city` VALUES (1,'Athens'),(2,'Thessaloniki'),(3,'Alexandroupoli');
/*!40000 ALTER TABLE `city` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `city_has_bins`
--

DROP TABLE IF EXISTS `city_has_bins`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `city_has_bins` (
  `City_City_Id` bigint(40) NOT NULL,
  `Bins_id` bigint(40) NOT NULL,
  PRIMARY KEY (`City_City_Id`,`Bins_id`),
  KEY `fk_City_has_Bins_Bins1_idx` (`Bins_id`),
  KEY `fk_City_has_Bins_City1_idx` (`City_City_Id`),
  CONSTRAINT `fk_City_has_Bins_Bins1` FOREIGN KEY (`Bins_id`) REFERENCES `bins` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_City_has_Bins_City1` FOREIGN KEY (`City_City_Id`) REFERENCES `city` (`City_Id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `city_has_bins`
--

LOCK TABLES `city_has_bins` WRITE;
/*!40000 ALTER TABLE `city_has_bins` DISABLE KEYS */;
/*!40000 ALTER TABLE `city_has_bins` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `credentials`
--

DROP TABLE IF EXISTS `credentials`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `credentials` (
  `Username` varchar(45) NOT NULL,
  `Password` varchar(500) NOT NULL,
  `Person_Id` bigint(40) NOT NULL,
  PRIMARY KEY (`Username`),
  KEY `fk_Credentials_People1_idx` (`Person_Id`),
  CONSTRAINT `fk_Credentials_People1` FOREIGN KEY (`Person_Id`) REFERENCES `people` (`Id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `credentials`
--

LOCK TABLES `credentials` WRITE;
/*!40000 ALTER TABLE `credentials` DISABLE KEYS */;
INSERT INTO `credentials` VALUES ('user1', 'sha1$6358380e$1$9acb22dc052e9255f1c985fbecb7cded06149d64', 1),('user2', 'sha1$24a65b26$1$5d56ae67ad946b00352b5178dbd5a564fdfba3cc', 2),('user3', 'sha1$2b1aedae$1$5f7e4bac8fa64873f3c9f25003c93d003d84c5a8', 3),('user4', 'sha1$311396e4$1$9e8e3f15509d6650c0480d8a2c7b52d7811a6327', 4),('user5', 'sha1$83ae18ea$1$dc35073fdaf05598f035ec152334a16d84158389', 5);
/*!40000 ALTER TABLE `credentials` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `credit_card`
--

DROP TABLE IF EXISTS `credit_card`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `credit_card` (
  `Credit_Card_Id` bigint(40) NOT NULL AUTO_INCREMENT,
  `Credit_Card_Holdername` varchar(45) NOT NULL,
  PRIMARY KEY (`Credit_Card_Id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `credit_card`
--

LOCK TABLES `credit_card` WRITE;
/*!40000 ALTER TABLE `credit_card` DISABLE KEYS */;
/*!40000 ALTER TABLE `credit_card` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `credit_card_has_bank`
--

DROP TABLE IF EXISTS `credit_card_has_bank`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `credit_card_has_bank` (
  `Credit_Card_Credit_Card_Id` bigint(40) NOT NULL,
  `Credit_Card_Bank_BankId` bigint(40) NOT NULL,
  `Bank_BankId` bigint(40) NOT NULL,
  `credit_card_has_bankcol` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`Credit_Card_Credit_Card_Id`,`Credit_Card_Bank_BankId`,`Bank_BankId`),
  KEY `fk_Credit_Card_has_Bank_Bank1_idx` (`Bank_BankId`),
  KEY `fk_Credit_Card_has_Bank_Credit_Card1_idx` (`Credit_Card_Credit_Card_Id`,`Credit_Card_Bank_BankId`),
  CONSTRAINT `fk_Credit_Card_has_Bank_Bank1` FOREIGN KEY (`Bank_BankId`) REFERENCES `bank` (`BankId`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_Credit_Card_has_Bank_Credit_Card1` FOREIGN KEY (`Credit_Card_Credit_Card_Id`) REFERENCES `credit_card` (`Credit_Card_Id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `credit_card_has_bank`
--

LOCK TABLES `credit_card_has_bank` WRITE;
/*!40000 ALTER TABLE `credit_card_has_bank` DISABLE KEYS */;
/*!40000 ALTER TABLE `credit_card_has_bank` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `debit_card`
--

DROP TABLE IF EXISTS `debit_card`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `debit_card` (
  `Debit_Card_Id` bigint(40) NOT NULL AUTO_INCREMENT,
  `Debit_Card_Holdername` varchar(45) NOT NULL,
  PRIMARY KEY (`Debit_Card_Id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `debit_card`
--

LOCK TABLES `debit_card` WRITE;
/*!40000 ALTER TABLE `debit_card` DISABLE KEYS */;
/*!40000 ALTER TABLE `debit_card` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `debit_card_has_bank`
--

DROP TABLE IF EXISTS `debit_card_has_bank`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `debit_card_has_bank` (
  `Debit_Card_Debit_Card_Id` bigint(40) NOT NULL,
  `Bank_BankId` bigint(40) NOT NULL,
  PRIMARY KEY (`Debit_Card_Debit_Card_Id`,`Bank_BankId`),
  KEY `fk_Debit_Card_has_Bank_Bank1_idx` (`Bank_BankId`),
  KEY `fk_Debit_Card_has_Bank_Debit_Card1_idx` (`Debit_Card_Debit_Card_Id`),
  CONSTRAINT `fk_Debit_Card_has_Bank_Bank1` FOREIGN KEY (`Bank_BankId`) REFERENCES `bank` (`BankId`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_Debit_Card_has_Bank_Debit_Card1` FOREIGN KEY (`Debit_Card_Debit_Card_Id`) REFERENCES `debit_card` (`Debit_Card_Id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `debit_card_has_bank`
--

LOCK TABLES `debit_card_has_bank` WRITE;
/*!40000 ALTER TABLE `debit_card_has_bank` DISABLE KEYS */;
/*!40000 ALTER TABLE `debit_card_has_bank` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `payment_options`
--

DROP TABLE IF EXISTS `payment_options`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `payment_options` (
  `Payment_Opt_Id` bigint(40) NOT NULL AUTO_INCREMENT,
  `Payment_Opt_Type` varchar(45) NOT NULL,
  `People_Id` bigint(40) NOT NULL,
  `Credit_Card_Credit_Card_Id` bigint(40) NOT NULL,
  `Credit_Card_Bank_BankId` bigint(40) NOT NULL,
  `Debit_Card_Debit_Card_Id` bigint(40) NOT NULL,
  `Prepaid_Prepaid_Id` bigint(40) NOT NULL,
  PRIMARY KEY (`Payment_Opt_Id`,`People_Id`,`Credit_Card_Credit_Card_Id`,`Credit_Card_Bank_BankId`,`Debit_Card_Debit_Card_Id`,`Prepaid_Prepaid_Id`),
  KEY `fk_Payment_Options_People1_idx` (`People_Id`),
  KEY `fk_Payment_Options_Credit_Card1_idx` (`Credit_Card_Credit_Card_Id`,`Credit_Card_Bank_BankId`),
  KEY `fk_Payment_Options_Debit_Card1_idx` (`Debit_Card_Debit_Card_Id`),
  KEY `fk_Payment_Options_Prepaid1_idx` (`Prepaid_Prepaid_Id`),
  CONSTRAINT `fk_Payment_Options_Credit_Card1` FOREIGN KEY (`Credit_Card_Credit_Card_Id`) REFERENCES `credit_card` (`Credit_Card_Id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_Payment_Options_Debit_Card1` FOREIGN KEY (`Debit_Card_Debit_Card_Id`) REFERENCES `debit_card` (`Debit_Card_Id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_Payment_Options_People1` FOREIGN KEY (`People_Id`) REFERENCES `people` (`Id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_Payment_Options_Prepaid1` FOREIGN KEY (`Prepaid_Prepaid_Id`) REFERENCES `prepaid` (`Prepaid_Id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `payment_options`
--

LOCK TABLES `payment_options` WRITE;
/*!40000 ALTER TABLE `payment_options` DISABLE KEYS */;
/*!40000 ALTER TABLE `payment_options` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `people`
--

DROP TABLE IF EXISTS `people`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `people` (
  `Id` bigint(40) NOT NULL AUTO_INCREMENT,
  `Name` varchar(45) NOT NULL,
  `Surname` varchar(45) NOT NULL,
  `Points` int(11) NOT NULL,
  `Image` varchar(45),
  `TotalPoints` int(11) DEFAULT NULL,
  `City_City_Id` bigint(40) NOT NULL,
  PRIMARY KEY (`Id`,`City_City_Id`),
  KEY `fk_People_City1_idx` (`City_City_Id`),
  CONSTRAINT `fk_People_City1` FOREIGN KEY (`City_City_Id`) REFERENCES `city` (`City_Id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `people`
--

LOCK TABLES `people` WRITE;
/*!40000 ALTER TABLE `people` DISABLE KEYS */;
INSERT INTO `people` VALUES (1,'Δημήτρης','Χριστοδούλου',500,'image_1499271000987.png',700,1),(2,'Κώστας','Παπαδόπουλος',168,'image_1499271801353.png',1501,1),(3,'Νίκος','Μανιατάκης',489,'profile_default.png',3000,1),(4,'Αντρέας','Σκαρμενούτσος',200,'profile_default.png',1800,1),(5,'Dimitris','Papakiriakopoulos',0,'profile_default.png',0,2);
/*!40000 ALTER TABLE `people` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `people_has_trophies`
--

DROP TABLE IF EXISTS `people_has_trophies`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `people_has_trophies` (
  `People_idPeople` bigint(40) NOT NULL,
  `Trophies_idTrophies` int(11) NOT NULL,
  PRIMARY KEY (`People_idPeople`,`Trophies_idTrophies`),
  KEY `fk_People_has_Trophies_Trophies1_idx` (`Trophies_idTrophies`),
  KEY `fk_People_has_Trophies_People1_idx` (`People_idPeople`),
  CONSTRAINT `fk_People_has_Trophies_People1` FOREIGN KEY (`People_idPeople`) REFERENCES `people` (`Id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_People_has_Trophies_Trophies1` FOREIGN KEY (`Trophies_idTrophies`) REFERENCES `trophies` (`Trophy_Id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `people_has_trophies`
--

LOCK TABLES `people_has_trophies` WRITE;
/*!40000 ALTER TABLE `people_has_trophies` DISABLE KEYS */;
INSERT INTO `people_has_trophies` VALUES (1,1),(1,3);
/*!40000 ALTER TABLE `people_has_trophies` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `people_services`
--

DROP TABLE IF EXISTS `people_services`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `people_services` (
  `People_idPeople` bigint(40) NOT NULL,
  `Services_idServices` bigint(40) NOT NULL,
  KEY `fk_People_has_Services_Services1_idx` (`Services_idServices`),
  KEY `fk_People_has_Services_People_idx` (`People_idPeople`),
  CONSTRAINT `fk_People_has_Services_People` FOREIGN KEY (`People_idPeople`) REFERENCES `people` (`Id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_People_has_Services_Services1` FOREIGN KEY (`Services_idServices`) REFERENCES `services` (`Service_Id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `people_services`
--

LOCK TABLES `people_services` WRITE;
/*!40000 ALTER TABLE `people_services` DISABLE KEYS */;
INSERT INTO `people_services` VALUES (1,3),(2,3);
/*!40000 ALTER TABLE `people_services` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `prepaid`
--

DROP TABLE IF EXISTS `prepaid`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `prepaid` (
  `Prepaid_Id` bigint(40) NOT NULL AUTO_INCREMENT,
  `Prepaid_Holdername` varchar(45) NOT NULL,
  PRIMARY KEY (`Prepaid_Id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `prepaid`
--

LOCK TABLES `prepaid` WRITE;
/*!40000 ALTER TABLE `prepaid` DISABLE KEYS */;
/*!40000 ALTER TABLE `prepaid` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `services`
--

DROP TABLE IF EXISTS `services`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `services` (
  `Service_Id` bigint(40) NOT NULL AUTO_INCREMENT,
  `Name` varchar(45) NOT NULL,
  `Empty_Slots` int(11) NOT NULL,
  `Points_Required` int(11) NOT NULL,
  `City_City_Id` bigint(40) NOT NULL,
  PRIMARY KEY (`Service_Id`,`City_City_Id`),
  KEY `fk_Services_City1_idx` (`City_City_Id`),
  CONSTRAINT `fk_Services_City1` FOREIGN KEY (`City_City_Id`) REFERENCES `city` (`City_Id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `services`
--

LOCK TABLES `services` WRITE;
/*!40000 ALTER TABLE `services` DISABLE KEYS */;
INSERT INTO `services` VALUES (1,'Κολυμβητήριο',8,60,1),(2,'Παραδοσιακοί',3,45,1),(3,'Latin',3,55,1),(4,'Θεατρική παράσταση',6,100,2);
/*!40000 ALTER TABLE `services` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `transaction`
--

DROP TABLE IF EXISTS `transaction`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `transaction` (
  `Transaction_Id` bigint(40) NOT NULL AUTO_INCREMENT,
  `Payment_Options_Payment_Opt_Id` bigint(40) NOT NULL,
  `Payment_Options_People_Id` bigint(40) NOT NULL,
  `Transaction_Date` datetime NOT NULL,
  `Transaction_Money_Amount` varchar(45) NOT NULL,
  PRIMARY KEY (`Transaction_Id`,`Payment_Options_Payment_Opt_Id`,`Payment_Options_People_Id`),
  KEY `fk_Transaction_Payment_Options1_idx` (`Payment_Options_Payment_Opt_Id`,`Payment_Options_People_Id`),
  CONSTRAINT `fk_Transaction_Payment_Options1` FOREIGN KEY (`Payment_Options_Payment_Opt_Id`, `Payment_Options_People_Id`) REFERENCES `payment_options` (`Payment_Opt_Id`, `People_Id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `transaction`
--

LOCK TABLES `transaction` WRITE;
/*!40000 ALTER TABLE `transaction` DISABLE KEYS */;
/*!40000 ALTER TABLE `transaction` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `trophies`
--

DROP TABLE IF EXISTS `trophies`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `trophies` (
  `Trophy_Id` int(11) NOT NULL AUTO_INCREMENT,
  `Trophy_Name` varchar(45) NOT NULL,
  `Trophy_Description` varchar(45) DEFAULT NULL,
  `Trophy_Image` varchar(45),
  PRIMARY KEY (`Trophy_Id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `trophies`
--

LOCK TABLES `trophies` WRITE;
/*!40000 ALTER TABLE `trophies` DISABLE KEYS */;
INSERT INTO `trophies` VALUES (1,'500 points','Get 500 points','ic_stars_black_24dp.png'),(2,'1000 points','Get 1000 points','ic_stars_black_24dp.png'),(3,'Fancy footwork','Redeem points for dance classes','ic_stars_black_24dp.png');
/*!40000 ALTER TABLE `trophies` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2017-08-04  1:59:22