DROP SCHEMA IF EXISTS bdservicio;
CREATE SCHEMA bdservicio DEFAULT CHARACTER SET utf8mb4;
USE bdservicio;

-- Quienes inician sesión: admin y empleados
CREATE TABLE usuario (
  idusuario INT UNSIGNED NOT NULL AUTO_INCREMENT,
  nombre    VARCHAR(60) NOT NULL,
  usuario   VARCHAR(30) NOT NULL,
  password  CHAR(64)    NOT NULL,              -- hash SHA-256, nunca la clave en texto
  rol       ENUM('ADMIN','EMPLEADO') NOT NULL,
  activo    TINYINT(1)  NOT NULL DEFAULT 1,
  PRIMARY KEY (idusuario),
  UNIQUE KEY uq_usuario (usuario)
) ENGINE = InnoDB;

-- Clientes: no inician sesión. El id es el número de cliente.
CREATE TABLE cliente (
  idcliente  INT UNSIGNED NOT NULL AUTO_INCREMENT,
  nombre     VARCHAR(45) NOT NULL,
  apellido   VARCHAR(45) NOT NULL,
  correo     VARCHAR(80) NULL,
  telefono   VARCHAR(20) NOT NULL,
  fecha_alta TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (idcliente)
) ENGINE = InnoDB;

-- Servicios y repuestos con precio preestablecido
CREATE TABLE item (
  iditem      INT UNSIGNED  NOT NULL AUTO_INCREMENT,
  descripcion VARCHAR(80)   NOT NULL,
  precio      DECIMAL(10,2) NOT NULL,
  PRIMARY KEY (iditem)
) ENGINE = InnoDB;

-- Orden de reparación
CREATE TABLE orden (
  idorden           INT UNSIGNED NOT NULL AUTO_INCREMENT,
  cliente_idcliente INT UNSIGNED NOT NULL,
  usuario_idusuario INT UNSIGNED NOT NULL,      -- empleado que la registró
  equipo            VARCHAR(80)  NOT NULL,
  falla             VARCHAR(255) NOT NULL,
  estado            ENUM('INGRESADO','EN_DIAGNOSTICO','EN_REPARACION','LISTO','ENTREGADO')
                    NOT NULL DEFAULT 'INGRESADO',
  fecha_ingreso     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
  observaciones     VARCHAR(255) NULL,
  PRIMARY KEY (idorden),
  CONSTRAINT fk_orden_cliente FOREIGN KEY (cliente_idcliente) REFERENCES cliente (idcliente),
  CONSTRAINT fk_orden_usuario FOREIGN KEY (usuario_idusuario) REFERENCES usuario (idusuario)
) ENGINE = InnoDB;

-- Ítems usados en cada orden (N:M con datos propios).
-- Se guarda el precio del momento: si el admin cambia el precio, las órdenes viejas no cambian.
CREATE TABLE orden_item (
  orden_idorden   INT UNSIGNED  NOT NULL,
  item_iditem     INT UNSIGNED  NOT NULL,
  cantidad        INT UNSIGNED  NOT NULL DEFAULT 1,
  precio_unitario DECIMAL(10,2) NOT NULL,
  PRIMARY KEY (orden_idorden, item_iditem),
  CONSTRAINT fk_oi_orden FOREIGN KEY (orden_idorden) REFERENCES orden (idorden),
  CONSTRAINT fk_oi_item  FOREIGN KEY (item_iditem)  REFERENCES item (iditem)
) ENGINE = InnoDB;

-- ----------------------------- Datos de prueba -----------------------------
-- Claves: admin / admin123   y   empleado / empleado123
INSERT INTO usuario (nombre, usuario, password, rol) VALUES
  ('Javier Rojas',  'admin',    SHA2('admin123', 256),    'ADMIN'),
  ('Lucía Márquez', 'empleado', SHA2('empleado123', 256), 'EMPLEADO');

INSERT INTO cliente (nombre, apellido, correo, telefono) VALUES
  ('Ana',    'Pérez',  'ana.perez@mail.com',  '2644123456'),
  ('Carlos', 'Gómez',  NULL,                  '2645987654');

INSERT INTO item (descripcion, precio) VALUES
  ('Diagnóstico',                 8000.00),
  ('Formateo e instalación de SO', 25000.00),
  ('Limpieza interna y pasta térmica', 18000.00),
  ('Disco SSD 480 GB',            45000.00),
  ('Memoria RAM 8 GB DDR4',       38000.00);

INSERT INTO orden (cliente_idcliente, usuario_idusuario, equipo, falla, estado) VALUES
  (1, 2, 'Notebook Lenovo IdeaPad 3', 'No enciende', 'EN_DIAGNOSTICO'),
  (2, 2, 'PC de escritorio',          'Muy lenta, se calienta', 'LISTO');

INSERT INTO orden_item (orden_idorden, item_iditem, cantidad, precio_unitario) VALUES
  (1, 1, 1, 8000.00),
  (2, 1, 1, 8000.00),
  (2, 3, 1, 18000.00),
  (2, 4, 1, 45000.00);
