@echo off
setlocal enabledelayedexpansion

set /p desc=Enter description:

for /f %%i in ('powershell -Command "Get-Date -Format yyyyMMddHHmmss"') do set datetime=%%i

set desc_clean=%desc: =_%
set desc_clean=%desc_clean:"=%

set filename=V%datetime%__%desc_clean%.sql

echo -- Migration file created at %datetime% > src\main\resources\db\migration\%filename%
echo Created: %filename%