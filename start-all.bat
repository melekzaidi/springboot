@echo off
echo ========================================
echo   Formation Management System
echo ========================================
echo.
echo Démarrage de l'application complète...
echo.

echo 1. Vérification des dépendances frontend...
cd formation-frontend
if not exist node_modules (
    echo Installation des dépendances npm...
    npm install
) else (
    echo Dépendances déjà installées.
)

echo.
echo 2. Démarrage du backend Spring Boot...
cd ..
start "Backend Spring Boot" cmd /k "mvn spring-boot:run"

echo.
echo 3. Attente du démarrage du backend (30 secondes)...
timeout /t 30 /nobreak

echo.
echo 4. Démarrage du frontend React...
cd formation-frontend
start "Frontend React" cmd /k "npm start"

echo.
echo ========================================
echo   Application démarrée avec succès!
echo ========================================
echo.
echo Backend: http://localhost:8081
echo Frontend: http://localhost:3000
echo API Docs: http://localhost:8081/swagger-ui.html
echo.
echo Appuyez sur une touche pour fermer cette fenêtre...
pause > nul