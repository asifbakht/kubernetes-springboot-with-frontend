CREATE TABLE IF NOT EXISTS `customer` (
    `id` VARCHAR(50) NOT NULL PRIMARY KEY,
    `first_name` varchar(50) NOT NULL,
    `last_name` varchar(50) NOT NULL,
    `email` varchar(80) NOT NULL,
    `date_of_birth` varchar(50) NOT NULL,
    `active` smallint DEFAULT false,
    `phone_number` varchar(12) DEFAULT NULL,
    `itin_or_ssn` varchar(10) NOT NULL,
    `date_created` timestamp DEFAULT NOW(),
    `date_updated` timestamp NOT NULL DEFAULT NOW() ON UPDATE NOW()
)ENGINE=InnoDB DEFAULT CHARSET=UTF8;