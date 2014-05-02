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
INSERT INTO `component_type` (`component_type_id`, `description`, `super_component_type`, `modified_at`, `modified_by`, `version`) VALUES ('a1','arer',NULL,'2014-03-18 13:08:50','test-user',0),('a2','a22222222222222',NULL,'2014-03-18 13:12:37','test-user',0),('a3','a333333333333333333','a1','2014-03-18 13:14:10','test-user',0),('a4','test a4','a3','2014-04-09 02:21:22','test-user',0),('aaaa','test ','a2','2014-03-18 13:19:25','test-user',0),('d1','ddd','ttt','2014-03-18 13:19:09','test-user',0),('Q','Quadrupole Magnet','_COMP','2014-04-09 08:54:43','test-user',0),('QSM1','Quadrupole ','Q','2014-04-09 08:53:27','test-user',0),('ttt','ttttttt',NULL,'2014-03-18 12:23:17','test-user',0),('_COMP','Generic Component','_COMP','2014-04-09 08:54:34','test-user',0);
/*!40000 ALTER TABLE `component_type` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `component_type_property`
--

LOCK TABLES `component_type_property` WRITE;
/*!40000 ALTER TABLE `component_type_property` DISABLE KEYS */;
INSERT INTO `component_type_property` (`component_type`, `property`, `type`, `value`, `modified_at`, `modified_by`, `version`) VALUES ('a1','Bending-Radius',NULL,NULL,'2014-04-07 16:57:49','user',0),('a2','Bending-Radius',NULL,'12','2014-04-09 02:39:15','user',0),('a3','Aperture','a',NULL,'2014-04-07 16:39:24','user',0),('QSM1','Aperture',NULL,NULL,'2014-04-09 09:59:14','user',0),('QSM1','Bending-Radius',NULL,NULL,'2014-04-09 09:59:21','user',0);
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
-- Dumping data for table `data_type`
--

LOCK TABLES `data_type` WRITE;
/*!40000 ALTER TABLE `data_type` DISABLE KEYS */;
INSERT INTO `data_type` (`data_type_id`, `description`, `modified_at`, `modified_by`, `version`) VALUES ('Aggregate','A mean value, standard deviation, and other meta data. Expresses the central tendency and dispersion of a set of data points','2014-02-26 00:00:00','system',0),('boolean','True or False','2014-02-26 00:00:00','system',0),('byte','8 bit signed integer','2014-02-26 00:00:00','system',0),('Continuum','Expersses a sequence of data points in time or frequency domain','2014-02-26 00:00:00','system',0),('double','double precision IEEE 754','2014-02-26 00:00:00','system',0),('Enum','An enumeration list and a value of that enumeration','2014-02-26 00:00:00','system',0),('File','A sequence of bytes','2014-02-26 00:00:00','system',0),('float','single precision IEEE 754','2014-02-26 00:00:00','system',0),('Histogram','An array of real number intervals, and their frequency counts. Expresses a 1D histogram.','2014-02-26 00:00:00','system',0),('Image','A general purpose pixel and meta data type, to encode image data of a single picture frame.','2014-02-26 00:00:00','system',0),('int','32 bit signed integer','2014-02-26 00:00:00','system',0),('long','64 bit signed integer','2014-02-26 00:00:00','system',0),('Matrix','A real number matrix','2014-02-26 00:00:00','system',0),('MultichannelArray','An array of PV names, their values, and metadata','2014-02-26 00:00:00','system',0),('NameValue','An array of scalar values where each element is named','2014-02-26 00:00:00','system',0),('ScalarArray','An array of scalar values of some single type. Compare with NTVariantArray','2014-02-26 00:00:00','system',0),('short','16 bit signed integer','2014-02-26 00:00:00','system',0),('string','UTF-8 ','2014-02-26 00:00:00','system',0),('Table','A table of scalars, where each column may be of different scalar array type','2014-02-26 00:00:00','system',0),('uint','32 bit unsigned integer','2014-02-26 00:00:00','system',0),('ulong','64 bit unsigned integer','2014-02-26 00:00:00','system',0),('URI','A structure for encapsulating a Uniform Resource Identifier (URI)','2014-02-26 00:00:00','system',0),('UserDefined','Defined by user','2014-02-26 00:00:00','system',0),('ushort','16 bit unsigned integer ','2014-02-26 00:00:00','system',0),('VariantArray','An array of some scalar type, where the type and values may be changed','2014-02-26 00:00:00','system',0);
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
-- Dumping data for table `physical_assembly`
--

LOCK TABLES `physical_assembly` WRITE;
/*!40000 ALTER TABLE `physical_assembly` DISABLE KEYS */;
/*!40000 ALTER TABLE `physical_assembly` ENABLE KEYS */;
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
INSERT INTO `property` (`property_id`, `description`, `association`, `unit`, `data_type`, `modified_at`, `modified_by`, `version`) VALUES ('Aperture','Radius aperture','T','meter','float','2014-03-08 15:51:42','test-user',0),('Bending-Angle','Bending angle of a magnet','T','degree (angle)','float','2014-03-08 15:50:36','test-user',0),('Bending-Radius','Bending radius','T','meter','float','2014-03-08 15:52:28','test-user',0),('Current','Current','T','ampere','float','2014-03-08 15:52:54','test-user',0),('Effective-F2F-Length','Effective flange to flange length','T','meter','float','2014-03-08 15:55:04','test-user',0),('Effective-Length','Effective length','T','meter','float','2014-03-08 15:54:05','test-user',0),('Maximum-Field','Maximum field of a magnet','T','tesla','float','2014-03-08 15:56:23','test-user',0),('Min-Beampipe-Inner-Dia','Minimum Deampipe innter diameter','T','meter','float','2014-03-08 15:57:27','test-user',0),('Number-Required','How many of such components are needed','T','ampere','uint','2014-03-08 16:00:31','test-user',0),('Power','Electric power','T','watt','float','2014-03-08 15:58:19','test-user',0),('Resistance','Electric resistance','T','ampere','float','2014-03-08 16:01:40','test-user',0),('Voltage','Electric voltage','T','volt','float','2014-03-08 16:02:33','test-user',0);
/*!40000 ALTER TABLE `property` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `unit`
--

LOCK TABLES `unit` WRITE;
/*!40000 ALTER TABLE `unit` DISABLE KEYS */;
INSERT INTO `unit` (`unit_id`, `quantity`, `symbol`, `description`, `base_unit_expr`, `modified_at`, `modified_by`, `version`) VALUES ('ampere','electric current','A','electric current',NULL,'2014-02-25 00:00:00','system',0),('ampere-per-meter','magnetic field strength  ','A/m','magnetic field strength  ',NULL,'2014-02-25 00:00:00','system',0),('ampere-per-square-meter','current density','A/m2','current density',NULL,'2014-02-25 00:00:00','system',0),('are','area','a','1 a = 1 dam2 = 102 m2',NULL,'2014-02-25 00:00:00','system',0),('astronomical unit','length','ua','1 ua = 1.495 98 x 1011 m, approximately',NULL,'2014-02-25 00:00:00','system',0),('bar','pressure','bar','1 bar = 0.1 MPa = 100 kPa = 1000 hPa = 105 Pa',NULL,'2014-02-25 00:00:00','system',0),('barn','area','b','1 b = 100 fm2 = 10-28 m2',NULL,'2014-02-25 00:00:00','system',0),('becquerel','activity','Bq','activity (of a radionuclide)','s-1','2014-02-25 00:00:00','system',0),('candela','luminous intensity','cd','luminous intensity',NULL,'2014-02-25 00:00:00','system',0),('candela-per-square-meter','luminance','cd/m2','luminance',NULL,'2014-02-25 00:00:00','system',0),('coulomb','electric charge','C','electric charge, quantity of electricity','s·A','2014-02-25 00:00:00','system',0),('coulomb-per-cubic-meter','electric charge density','C/m3','electric charge density',NULL,'2014-02-25 00:00:00','system',0),('coulomb-per-kilogram','exposure','C/kg','exposure (x and  rays)',NULL,'2014-02-25 00:00:00','system',0),('coulomb-per-square-meter','electric flux density','C/m2','electric flux density',NULL,'2014-02-25 00:00:00','system',0),('cubic-meter','volume','m3','volume',NULL,'2014-02-25 00:00:00','system',0),('cubic-meter-per-kilogram','specific volume','m3/kg','specific volume',NULL,'2014-02-25 00:00:00','system',0),('curie','radiation','Ci','1 Ci = 3.7 x 1010 Bq',NULL,'2014-02-25 00:00:00','system',0),('day','time','d','1 d = 24 h = 86 400 s',NULL,'2014-02-25 00:00:00','system',0),('degree (angle)','angle','deg','1° = ( PI/180) rad',NULL,'2014-02-25 00:00:00','system',0),('degree-Celsius','Celsius temperature','°C','Celsius temperature','K','2014-02-25 00:00:00','system',0),('electronvolt','electric potential difference','eV','1 eV = 1.602 18 x 10-19 J, approximately',NULL,'2014-02-25 00:00:00','system',0),('farad','capacitance','F','capacitance','m-2·kg-1·s4·A2','2014-02-25 00:00:00','system',0),('farad-per-meter','permittivity','F/m','permittivity',NULL,'2014-02-25 00:00:00','system',0),('gray','absorbed dose','Gy','absorbed dose, specific energy (imparted), kerma','m2·s-2','2014-02-25 00:00:00','system',0),('gray-per-second','absorbed dose rate','Gy/s','absorbed dose rate',NULL,'2014-02-25 00:00:00','system',0),('hectare ','area','ha','1 ha = 1 hm2 = 104 m2',NULL,'2014-02-25 00:00:00','system',0),('henry','inductance','H','inductance','m2·kg·s-2·A-2','2014-02-25 00:00:00','system',0),('henry-per-meter','permeability','H/m','permeability',NULL,'2014-02-25 00:00:00','system',0),('hertz','frequency','Hz','frequency','s-1','2014-02-25 00:00:00','system',0),('hour','time','h','1 h = 60 min = 3600 s',NULL,'2014-02-25 00:00:00','system',0),('joule','energy','J','energy, work, quantity of heat  ','m2·kg·s-2','2014-02-25 00:00:00','system',0),('joule-per-cubic-meter','energy density','J/m3','energy density',NULL,'2014-02-25 00:00:00','system',0),('joule-per-kelvin','entropy','J/K','heat capacity, entropy',NULL,'2014-02-25 00:00:00','system',0),('joule-per-kilogram','specific energy','J/kg','specific energy',NULL,'2014-02-25 00:00:00','system',0),('joule-per-kilogram-kelvin','specific heat capacity','J/(kg·K)','specific heat capacity, specific entropy',NULL,'2014-02-25 00:00:00','system',0),('joule-per-mole','molar energy','J/mol','molar energy',NULL,'2014-02-25 00:00:00','system',0),('joule-per-mole-kelvin','molar entropy','J/(mol·K)','molar entropy, molar heat capacity',NULL,'2014-02-25 00:00:00','system',0),('katal','catalytic activity','kat','catalytic activity','s-1·mol','2014-02-25 00:00:00','system',0),('katal-per-cubic-meter','catalytic (activity) concentration','kat/m3','catalytic (activity) concentration',NULL,'2014-02-25 00:00:00','system',0),('kelvin','thermodynamic temperature','K','thermodynamic temperature',NULL,'2014-02-25 00:00:00','system',0),('kilogram','mass','kg','mass',NULL,'2014-02-25 00:00:00','system',0),('kilogram-per-cubic-meter','mass density','kg/m3','mass density',NULL,'2014-02-25 00:00:00','system',0),('liter','volume','L','1 L = 1 dm3 = 10-3 m3',NULL,'2014-02-25 00:00:00','system',0),('lumen','luminous flux','lm','luminous flux','cd','2014-02-25 00:00:00','system',0),('lux','illuminance','lx','illuminance','m-2·cd','2014-02-25 00:00:00','system',0),('meter','length','m','length',NULL,'2014-02-25 00:00:00','system',0),('meter-per-second','speed, velocity','m/s','speed, velocity',NULL,'2014-02-25 00:00:00','system',0),('meter-per-second-squared  ','acceleration','m/s2','acceleration',NULL,'2014-02-25 00:00:00','system',0),('metric ton','weight','t','1 t = 103 kg',NULL,'2014-02-25 00:00:00','system',0),('minute (angle)','angle','min','1 minute = (1/60)° = (/10 800) rad',NULL,'2014-02-25 00:00:00','system',0),('minute (time)','time','min','1 min = 60 s',NULL,'2014-02-25 00:00:00','system',0),('mole','amount of substance','mol','amount of substance',NULL,'2014-02-25 00:00:00','system',0),('mole-per-cubic-meter','amount-of-substance','mol/m3','amount-of-substance concentration',NULL,'2014-02-25 00:00:00','system',0),('newton','force','N','force','m·kg·s-2','2014-02-25 00:00:00','system',0),('newton-meter','moment of force','N·m','moment of force',NULL,'2014-02-25 00:00:00','system',0),('newton-per-meter','surface tension','N/m','surface tension',NULL,'2014-02-25 00:00:00','system',0),('pascal','pressure, stress','Pa','pressure, stress','m-1·kg·s-2','2014-02-25 00:00:00','system',0),('pascal-second','dynamic viscosity','Pa·s','dynamic viscosity',NULL,'2014-02-25 00:00:00','system',0),('rad','radiation','rad','1 rad = 1 cGy = 10-2 Gy',NULL,'2014-02-25 00:00:00','system',0),('radian','plane angle','rad','plane angle',NULL,'2014-02-25 00:00:00','system',0),('radian-per-second','angular velocity','rad/s','angular velocity',NULL,'2014-02-25 00:00:00','system',0),('radian-per-second-squared','angular acceleration','rad/s2','angular acceleration',NULL,'2014-02-25 00:00:00','system',0),('reciprocal-meter','wave number','m-1','wave number',NULL,'2014-02-25 00:00:00','system',0),('rem','radiation','rem','1 rem = 1 cSv = 10-2 Sv',NULL,'2014-02-25 00:00:00','system',0),('roentgen','radiation','R','1 R = 2.58 x 10-4 C/kg',NULL,'2014-02-25 00:00:00','system',0),('second','time','s','time',NULL,'2014-02-25 00:00:00','system',0),('second (angle)','angle','sec','1 second = (1/60) = (/648 000) rad',NULL,'2014-02-25 00:00:00','system',0),('siemens','electric conductance','S','electric conductance','m-2·kg-1·s3·A2','2014-02-25 00:00:00','system',0),('sievert','dose equivalent','Sv','dose equivalent (d)','m2·s-2','2014-02-25 00:00:00','system',0),('square-meter','area','m2','area',NULL,'2014-02-25 00:00:00','system',0),('steradian','solid angle','sr','solid angle',NULL,'2014-02-25 00:00:00','system',0),('tesla','magnetic flux density','T','magnetic flux density','kg·s-2·A-1','2014-02-25 00:00:00','system',0),('unified atomic mass unit','mass','u','1 u = 1.660 54 x 10-27 kg, approximately',NULL,'2014-02-25 00:00:00','system',0),('volt','electric potential difference','V','electric potential difference,','m2·kg·s-3·A-1','2014-02-25 00:00:00','system',0),('volt-per-meter','electric field strength','V/m','electric field strength',NULL,'2014-02-25 00:00:00','system',0),('watt','power, radiant flux','W','power, radiant flux','m2·kg·s-3','2014-02-25 00:00:00','system',0),('watt-per-meter-kelvin','thermal conductivity','W/(m·K)','thermal conductivity',NULL,'2014-02-25 00:00:00','system',0),('watt-per-square-meter','heat flux density','W/m2','heat flux density, irradiance',NULL,'2014-02-25 00:00:00','system',0),('watt-per-square-meter-steradian','radiance','W/(m2·sr)','radiance',NULL,'2014-02-25 00:00:00','system',0),('watt-per-steradian','radiant intensity','W/sr','radiant intensity',NULL,'2014-02-25 00:00:00','system',0),('weber','magnetic flux','Wb','magnetic flux','m2·kg·s-2·A-1','2014-02-25 00:00:00','system',0),('ångström','length','Å','1 Å = 0.1 nm = 10-10 m',NULL,'2014-02-25 00:00:00','system',0);
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

-- Dump completed on 2014-04-11 16:26:46
