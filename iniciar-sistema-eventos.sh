#!/bin/bash

#  CONFIGURACIÓN DE COLORES
RESET="\033[0m"
if [[ "$COLORTERM" == "truecolor" || "$COLORTERM" == "24bit" ]]; then
  TEAL="\033[38;2;3;252;177m"
else
  TEAL="\033[0;36m" # fallback cyan
fi
YELLOW="\033[1;33m"
GREEN="\033[1;32m"
RED="\033[1;31m"
BOLD="\033[1m"

echo
echo -e "${TEAL}${BOLD} ------------------------------${RESET}"
echo -e "${TEAL}${BOLD} 🚀 LANZANDO SISTEMA DE EVENTOS${RESET}"
echo -e "${TEAL}${BOLD} ------------------------------${RESET}"
echo
sleep 1

# DETECCIÓN DE TERMINAL
detect_terminal() {
  local terminals=("x-terminal-emulator" "gnome-terminal" "konsole" "kitty" "alacritty" "xfce4-terminal")

  for term in "${terminals[@]}"; do
   if command -v "$term" &> /dev/null; then
    echo "$term"
    return
   fi
  done
}

TERMINAL_APP=$(detect_terminal)

# INPUT MANUAL
if [ -z "$TERMINAL_APP" ]; then
  echo -e "${YELLOW}${BOLD} ⚠️  No se detectó una terminal conocida automáticamente.${RESET}"
  echo -e "${BOLD} Por favor, ingrese el comando de su terminal (ej: gnome-terminal) o presione ENTER para ver los comandos manuales:${RESET}"
  read USER_INPUT

  if [ -n "$USER_INPUT" ]; then
      if command -v "$USER_INPUT" &> /dev/null; then
          TERMINAL_APP="$USER_INPUT"
      else
          echo -e "${RED} ❌ Error: No se puede ejecutar '$USER_INPUT' (posible entorno restringido).${RESET}"
      fi
  fi
fi

#  CONFIGURACIÓN DE BANDERAS
if [ -n "$TERMINAL_APP" ]; then
  echo -e "${GREEN}${BOLD} ✅ Terminal detectada:${RESET} $TERMINAL_APP"

  case "$TERMINAL_APP" in
    "gnome-terminal")
      TERM_FLAG="--" ;;
    "x-terminal-emulator")
      TERM_FLAG="-e" ;;
    "konsole")
      TERM_FLAG="-e" ;;
    "xfce4-terminal")
      TERM_FLAG="-x" ;;
    *)
      TERM_FLAG="-e" ;;
  esac
fi

echo

# COMANDOS A EJECUTAR
CMD_BACKEND="echo -e '${TEAL}${BOLD} -----------------------${RESET}'; echo -e '${TEAL}${BOLD} ⚙️  Iniciando Backend...${RESET}'; echo -e '${TEAL}${BOLD} -----------------------${RESET}'; sleep 1; echo; cd backend; ./mvnw -Pdev spring-boot:run"
CMD_PROXY="echo -e '${TEAL}${BOLD} ---------------------${RESET}'; echo -e '${TEAL}${BOLD} ⚙️  Iniciando Proxy...${RESET}'; echo -e '${TEAL}${BOLD} ---------------------${RESET}'; sleep 1; echo; cd proxy; ./mvnw spring-boot:run"

#  EJECUCIÓN O FALLBACK
if [ -n "$TERMINAL_APP" ]; then
    sleep 1
    echo -e "${YELLOW}${BOLD} 🖥️  Abriendo ventana para Backend (Puerto 8080)...${RESET}"
    $TERMINAL_APP $TERM_FLAG bash -c "$CMD_BACKEND; exec bash" &

    sleep 3 # Espera para no saturar

    echo -e "${YELLOW}${BOLD} 🖥️  Abriendo ventana para Proxy (Puerto 8081)...${RESET}"
    $TERMINAL_APP $TERM_FLAG bash -c "$CMD_PROXY; exec bash" &

    echo
    echo -e "${GREEN}${BOLD} ✅ Ventanas lanzadas.${RESET}"
    echo -e "${BOLD} ℹ️  Para detener el sistema, simplemente cierre las ventanas nuevas.${RESET}"
    echo

else
    # FALLBACK
    echo -e "${RED}${BOLD} ❌ No se pudo lanzar una terminal externa.${RESET}"
    echo -e "${YELLOW} Posible causa: Está en un entorno restringido (ej: IntelliJ/Flatpak).${RESET}"
    echo
    echo -e "${BOLD} 💡  Por favor, ejecute estos comandos en dos terminales separadas:${RESET}"
    echo -e "${TEAL}${BOLD} ⚙️  Terminal 1 (Backend):${RESET}"
    echo -e "    cd backend && ./mvnw -Pdev spring-boot:run"
    echo
    echo -e "${TEAL}${BOLD} ⚙️  Terminal 2 (Proxy):${RESET}"
    echo -e "    cd proxy && ./mvnw spring-boot:run"
    echo
fi