-- phpMyAdmin SQL Dump
-- version 4.1.12
-- http://www.phpmyadmin.net
--
-- Servidor: localhost
-- Tiempo de generación: 14-02-2016 a las 17:24:36
-- Versión del servidor: 5.5.36
-- Versión de PHP: 5.4.27

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Base de datos: `suitecrm`
--

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `adm_formatos_impresion`
--

CREATE TABLE IF NOT EXISTS `adm_formatos_impresion` (
  `id` varchar(36) NOT NULL DEFAULT '',
  `name` varchar(150) DEFAULT NULL,
  `deleted` smallint(6) DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Volcado de datos para la tabla `adm_formatos_impresion`
--

INSERT INTO `adm_formatos_impresion` (`id`, `name`, `deleted`) VALUES
('CONTRATO_ADMON_VIVIENDA', 'Contrato Administracion Vivienda', 0),
('CONTRATO_ARRENDAMIENTO', 'Contrato Arrendamiento Vivienda', 0);

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
