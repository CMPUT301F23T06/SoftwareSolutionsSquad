#!/bin/bash

# Colored output
BL='\033[0;34m'
G='\033[0;32m'
RED='\033[0;31m'
YE='\033[1;33m'
NC='\033[0m' # No Color

# Emulator name variable
emulator_name=${EMULATOR_NAME:-"emulator"}

# Function to check hardware acceleration
function check_hardware_acceleration() {
    if [[ "$HW_ACCEL_OVERRIDE" != "" ]]; then
        hw_accel_flag="$HW_ACCEL_OVERRIDE"
    else
        if [[ "$OSTYPE" == "darwin"* ]]; then
            # macOS-specific hardware acceleration check
            HW_ACCEL_SUPPORT=$(sysctl -a | grep -E -c '(vmx|svm)')
        else
            # generic Linux hardware acceleration check
            HW_ACCEL_SUPPORT=$(grep -E -c '(vmx|svm)' /proc/cpuinfo)
        fi

        hw_accel_flag=$HW_ACCEL_SUPPORT == 0 ? "-accel off" : "-accel on"
    fi

    echo "$hw_accel_flag"
}

# Function to launch the emulator
function launch_emulator() {
    adb devices | grep emulator | cut -f1 | xargs -I {} adb -s "{}" emu kill
    hw_accel_flag=$(check_hardware_acceleration)
    options="@${emulator_name} -no-window -no-snapshot -screen no-touch -noaudio -memory 1024 -no-boot-anim ${hw_accel_flag} -camera-back none"

    if [[ "$OSTYPE" == *linux* ]]; then
        echo "${OSTYPE}: emulator ${options} -gpu off"
        emulator $options -gpu off &
    elif [[ "$OSTYPE" == *darwin* ]] || [[ "$OSTYPE" == *macos* ]]; then
        echo "${OSTYPE}: emulator ${options} -gpu swiftshader_indirect"
        emulator $options -gpu swiftshader_indirect &
    else
        echo "Unsupported OS"
        exit 1
    fi

    if [ $? -ne 0 ]; then
        echo -e "${RED}Error launching emulator${NC}"
        exit 1
    fi
}

# Function to check the emulator status
function check_emulator_status() {
    printf "${G}==> ${BL}Checking emulator booting up status 🧐${NC}\n"
    start_time=$(date +%s)
    timeout=${EMULATOR_TIMEOUT:-1200}  # Increased timeout to 1200 seconds
    adb wait-for-device  # Wait for device to be recognized by ADB
    while true; do
        result=$(adb shell getprop sys.boot_completed 2>&1)
        if [[ "$result" == "1" ]]; then
            printf "${G}==> \u2713 Emulator is ready${NC}\n"
            adb devices -l
            ensure_emulator_focus
            break
        else
            printf "${RED}==> Emulator is not ready yet. Status: $result ${NC}\n"
            current_time=$(date +%s)
            elapsed_time=$((current_time - start_time))
            if [ $elapsed_time -ge $timeout ]; then
                printf "${RED}==> Timeout after ${timeout} seconds${NC}\n"
                exit 1
            fi
            sleep 5
        fi
    done
}

# Function to ensure the emulator has window focus
function ensure_emulator_focus() {
    adb shell input keyevent 82  # Menu key
    adb shell input keyevent 66  # Enter key
}

# Function to disable animations on the emulator for faster testing
function disable_animation() {
    adb shell "settings put global window_animation_scale 0.0"
    adb shell "settings put global transition_animation_scale 0.0"
    adb shell "settings put global animator_duration_scale 0.0"
}

# Function to handle hidden API policies (necessary for some Android versions)
function hidden_policy() {
    adb shell "settings put global hidden_api_policy_pre_p_apps 1"
    adb shell "settings put global hidden_api_policy_p_apps 1"
    adb shell "settings put global hidden_api_policy 1"
}

# Main logic to handle prelaunch and postlaunch
case "$1" in
    prelaunch)
        launch_emulator
        check_emulator_status
        ;;
    postlaunch)
        disable_animation
        hidden_policy
        ;;
    *)
        echo "Invalid or no argument provided. Please use 'prelaunch' or 'postlaunch'."
        exit 1
        ;;
esac
