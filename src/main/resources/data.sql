DROP TABLE IF EXISTS widgets;

CREATE TABLE widgets (
  id INT AUTO_INCREMENT  PRIMARY KEY,
  x INT NOT NULL,
  y INT NOT NULL,
  z_index INT NOT NULL,
  width INT NOT NULL,
  height INT NOT NULL,
  last_modification DATE NOT NULL
);