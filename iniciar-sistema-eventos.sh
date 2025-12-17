#!/bin/bash

# Colores del script
RESET="\033[0m"
if [[ "$COLORTERM" == "truecolor" || "$COLORTERM" == "24bit" ]]; then
	TEAL="\033[38;2;3;252;177m"
else
	TEAL="\033[0;36m" # fallback cyan
fi
YELLOW="\033[1;33m"
GREEN="\033[1;32m"
BOLD="\033[1m"

echo
echo -e "${TEAL}${BOLD} ------------------------------${RESET}"
echo -e "${TEAL}${BOLD} 🚀 LANZANDO SISTEMA DE EVENTOS${RESET}"
echo -e "${TEAL}${BOLD} ------------------------------${RESET}"
echo
sleep 1

# DETECCIÓN DE TERMINAL
detect_terminal() {
	# Terminales
	# gnome-terminal: Común en Ubuntu/Mint/Fedora
	# konsole: Común en KDE (Arch/Manjaro)
	# kitty / alacritty: Comunes en Arch (Tiling WMs)
	# xfce4-terminal: Común en XFCE (Mint Ligero)
	local terminals=("gnome-terminal" "konsole" "kitty" "alacritty" "xfce4-terminal" "x-terminal-emulator")
	
	for term in "${terminals[@]}"; do
		if command -v "$term" &> /dev/null; then
			echo "$term"
			return
		fi
	done
}

TERMINAL_APP=$(detect_terminal)

if [ -z "$TERMINAL_APP" ]; then
	echo -e "${YELLOW}${BOLD} ⚠️  No se detectó una terminal conocida automáticamente.${RESET}"
	echo "Por favor, ingrese el comando de su terminal (ej: gnome-terminal):"
	read TERMINAL_APP
else
	# CORRECCIÓN 1: Cambiado ${NC} por ${RESET}
	echo -e "${GREEN}${BOLD} ✅ Terminal detectada:${RESET} $TERMINAL_APP"
fi

# Configurar el flag correcto según la terminal (Gnome usa --, el resto suele usar -e)
if [[ "$TERMINAL_APP" == "gnome-terminal" ]]; then
    TERM_FLAG="--"
else
    TERM_FLAG="-e"
fi
echo

# INICIO DE SERVICIOS
sleep 1
# Iniciar Backend
echo -e "${YELLOW}${BOLD} 🖥️  Abriendo ventana para Backend (Puerto 8080)...${RESET}"
sleep 1
$TERMINAL_APP $TERM_FLAG bash -c "echo -e '${TEAL}${BOLD} -----------------------${RESET}'; echo -e '${TEAL}${BOLD} ⚙️  Iniciando Backend...${RESET}'; echo -e '${TEAL}${BOLD} -----------------------${RESET}'; sleep 1; echo; cd backend; ./mvnw -Pdev spring-boot:run; exec bash"

# Esperar un poco para no saturar el arranque
sleep 3
echo

# Iniciar Proxy
echo -e "${YELLOW}${BOLD} 🖥️  Abriendo ventana para Proxy (Puerto 8081)...${RESET}"
sleep 1
$TERMINAL_APP $TERM_FLAG bash -c "echo -e '${TEAL}${BOLD} ---------------------${RESET}'; echo -e '${TEAL}${BOLD} ⚙️  Iniciando Proxy...${RESET}'; echo -e '${TEAL}${BOLD} ---------------------${RESET}'; sleep 1; echo; cd proxy; ./mvnw spring-boot:run; exec bash"

echo
echo -e "${GREEN}${BOLD} ✅ Ventanas lanzadas.${RESET}"
echo
echo -e "${BOLD} ℹ️  Para detener el sistema, simplemente cierre las ventanas nuevas.${RESET}"
echo
