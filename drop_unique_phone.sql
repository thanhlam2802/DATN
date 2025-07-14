SELECT kc.name AS ConstraintName, c.name AS ColumnName
FROM sys.key_constraints kc
         JOIN sys.index_columns ic ON kc.parent_object_id = ic.object_id AND kc.unique_index_id = ic.index_id
         JOIN sys.columns c ON ic.object_id = c.object_id AND ic.column_id = c.column_id
WHERE kc.[type] = 'UQ' AND OBJECT_NAME(kc.parent_object_id) = 'users' AND c.name = 'phone';

ALTER TABLE users drop CONSTRAINT UQ__users__B43B145F9352AE51;