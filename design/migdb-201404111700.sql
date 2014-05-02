-- MySQL dump 10.13  Distrib 5.1.66, for debian-linux-gnu (x86_64)
--
-- Host: localhost    Database: discs_conf20
-- ------------------------------------------------------
-- Server version	5.1.66-0+squeeze1

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
-- Dumping data for table `comp_type_asm`
--

LOCK TABLES `comp_type_asm` WRITE;
/*!40000 ALTER TABLE `comp_type_asm` DISABLE KEYS */;
/*!40000 ALTER TABLE `comp_type_asm` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `comp_type_member`
--

LOCK TABLES `comp_type_member` WRITE;
/*!40000 ALTER TABLE `comp_type_member` DISABLE KEYS */;
/*!40000 ALTER TABLE `comp_type_member` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `component_pair`
--

LOCK TABLES `component_pair` WRITE;
/*!40000 ALTER TABLE `component_pair` DISABLE KEYS */;
/*!40000 ALTER TABLE `component_pair` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `component_relation`
--

LOCK TABLES `component_relation` WRITE;
/*!40000 ALTER TABLE `component_relation` DISABLE KEYS */;
/*!40000 ALTER TABLE `component_relation` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `component_type`
--

LOCK TABLES `component_type` WRITE;
/*!40000 ALTER TABLE `component_type` DISABLE KEYS */;
/*!40000 ALTER TABLE `component_type` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `component_type_property`
--

LOCK TABLES `component_type_property` WRITE;
/*!40000 ALTER TABLE `component_type_property` DISABLE KEYS */;
/*!40000 ALTER TABLE `component_type_property` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `config`
--

LOCK TABLES `config` WRITE;
/*!40000 ALTER TABLE `config` DISABLE KEYS */;
/*!40000 ALTER TABLE `config` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `ct_artifact`
--

LOCK TABLES `ct_artifact` WRITE;
/*!40000 ALTER TABLE `ct_artifact` DISABLE KEYS */;
/*!40000 ALTER TABLE `ct_artifact` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `data_type`
--

LOCK TABLES `data_type` WRITE;
/*!40000 ALTER TABLE `data_type` DISABLE KEYS */;
/*!40000 ALTER TABLE `data_type` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `installation_record`
--

LOCK TABLES `installation_record` WRITE;
/*!40000 ALTER TABLE `installation_record` DISABLE KEYS */;
/*!40000 ALTER TABLE `installation_record` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `layout_slot`
--

LOCK TABLES `layout_slot` WRITE;
/*!40000 ALTER TABLE `layout_slot` DISABLE KEYS */;
/*!40000 ALTER TABLE `layout_slot` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `layout_slot_asm`
--

LOCK TABLES `layout_slot_asm` WRITE;
/*!40000 ALTER TABLE `layout_slot_asm` DISABLE KEYS */;
/*!40000 ALTER TABLE `layout_slot_asm` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `lc_artifact`
--

LOCK TABLES `lc_artifact` WRITE;
/*!40000 ALTER TABLE `lc_artifact` DISABLE KEYS */;
/*!40000 ALTER TABLE `lc_artifact` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `logical_component`
--

LOCK TABLES `logical_component` WRITE;
/*!40000 ALTER TABLE `logical_component` DISABLE KEYS */;
/*!40000 ALTER TABLE `logical_component` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `logical_component_property`
--

LOCK TABLES `logical_component_property` WRITE;
/*!40000 ALTER TABLE `logical_component_property` DISABLE KEYS */;
/*!40000 ALTER TABLE `logical_component_property` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `pc_artifact`
--

LOCK TABLES `pc_artifact` WRITE;
/*!40000 ALTER TABLE `pc_artifact` DISABLE KEYS */;
/*!40000 ALTER TABLE `pc_artifact` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `physical_assembly`
--

LOCK TABLES `physical_assembly` WRITE;
/*!40000 ALTER TABLE `physical_assembly` DISABLE KEYS */;
/*!40000 ALTER TABLE `physical_assembly` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `physical_comp_asm`
--

LOCK TABLES `physical_comp_asm` WRITE;
/*!40000 ALTER TABLE `physical_comp_asm` DISABLE KEYS */;
/*!40000 ALTER TABLE `physical_comp_asm` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `physical_component`
--

LOCK TABLES `physical_component` WRITE;
/*!40000 ALTER TABLE `physical_component` DISABLE KEYS */;
/*!40000 ALTER TABLE `physical_component` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `physical_component_property`
--

LOCK TABLES `physical_component_property` WRITE;
/*!40000 ALTER TABLE `physical_component_property` DISABLE KEYS */;
/*!40000 ALTER TABLE `physical_component_property` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `property`
--

LOCK TABLES `property` WRITE;
/*!40000 ALTER TABLE `property` DISABLE KEYS */;
/*!40000 ALTER TABLE `property` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `unit`
--

LOCK TABLES `unit` WRITE;
/*!40000 ALTER TABLE `unit` DISABLE KEYS */;
/*!40000 ALTER TABLE `unit` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2014-04-11 17:00:29
