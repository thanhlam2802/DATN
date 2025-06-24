@echo off
setlocal enabledelayedexpansion

set /p desc=Enter description:

REM Get current datetime
for /f "tokens=1-4 delims=/ " %%a in ("%date%") do (
    set yyyy=%%d
    set mm=%%b
    set dd=%%c
)

for /f "tokens=1-4 delims=:. " %%a in ("%time%") do (
    set hh=%%a
    set min=%%b
    set sec=%%c
)

REM Pad single digits with 0
if 1%hh% LSS 20 set hh=0%hh%
if 1%min% LSS 20 set min=0%min%
if 1%sec% LSS 20 set sec=0%sec%

REM Clean description
set desc_clean=%desc: =_%
set desc_clean=%desc_clean:"=%

REM Final version string
set datetime=%yyyy%%mm%%dd%_%hh%%min%%sec%
set filename=V%datetime%__%desc_clean%.sql

REM Create file
echo -- Migration file created on %date% at %time% > src\main\resources\db\migration\%filename%
echo Created: %filename%
