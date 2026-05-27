-- Ejecuta este script en SQL Server Management Studio o en sqlcmd
-- Conectar a: jhon\MOVILES, Base de datos: NorthWindDB

ALTER TABLE [dbo].[Products]
ADD [Description] NVARCHAR(500) NULL;

-- Verificar que se creó la columna
SELECT COLUMN_NAME, DATA_TYPE, CHARACTER_MAXIMUM_LENGTH, IS_NULLABLE
FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_NAME = 'Products'
ORDER BY ORDINAL_POSITION;
