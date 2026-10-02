# ♻️ localhost-kaucjomat

<div align="center">

![Minecraft Version](https://img.shields.io/badge/Minecraft-1.20+-brightgreen.svg?style=for-the-badge&logo=minecraft)
![Java](https://img.shields.io/badge/Java-17+-orange.svg?style=for-the-badge&logo=openjdk)
![Platform](https://img.shields.io/badge/Platform-Paper%20%7C%20Spigot-blue.svg?style=for-the-badge)
![Vault](https://img.shields.io/badge/Dependency-Vault-yellow.svg?style=for-the-badge)

**A modern, exploit-proof Bottle Deposit / Reverse Vending Machine (*Kaucjomat*) plugin for Minecraft servers.**

[Features](#-key-features) • [Installation](#-installation) • [Commands & Permissions](#-commands--permissions) • [Configuration](#-configuration) • [Building](#-building-from-source)

---

</div>

## 📖 Overview

**localhost-kaucjomat** introduces an interactive recycling system to your server economy. Players can collect designated deposit bottles and exchange them for currency using a custom, intuitive GUI.

Designed with server stability and security in mind, the plugin utilizes modern Bukkit **PersistentDataContainer (PDC)** tags to ensure players cannot exploit standard bottles or counterfeit items via anvils.

---

## ✨ Key Features

- 🔒 **Exploit-Safe Item Validation**: Validates bottles using persistent NBT/PDC keys (`is_kaucjomat_bottle`). Renamed glass bottles or regular items cannot be used to dupe money.
- 🎛️ **Interactive Recycler GUI**: Clean inventory GUI allowing players to deposit multiple bottles at once with a single-click payout button.
- 💸 **Vault Economy Integration**: Automatically awards currency via Vault directly to the player's account.
- 🛡️ **Anti-Loss Protection**: If a player closes the GUI before exchanging, items are safely returned to their inventory. If their inventory is full, remaining items drop at their feet.
- 📊 **Partial Exchange Handling**: Leftover bottles that do not form a complete exchange set are kept safe in the GUI.
- 🎨 **100% Configurable**: Customize GUI title, slot positions, materials, exchange requirements, rewards, and all messages with color code (`&`) support.

---

## 📋 Requirements

| Requirement | Supported Version |
| :--- | :--- |
| **Server Software** | [Paper](https://papermc.io), Purpur, or Spigot **1.20+** |
| **Java** | **Java 17** or newer |
| **Dependencies** | [Vault](https://www.spigotmc.org/resources/vault.34315/) + any Vault-supported economy plugin (e.g., EssentialsX) |

---

## 📥 Installation

1. Make sure you have **Java 17+** and **Vault** (along with an economy plugin like EssentialsX) installed on your server.
2. Download the latest `localhost-kaucjomat.jar` from releases or build it from source.
3. Place the `.jar` file into your server's `plugins/` directory.
4. Restart your server to generate the configuration files.
5. Edit `plugins/localhost-kaucjomat/config.yml` according to your needs.
6. Apply changes using `/kaucjomatreload`.

---

## 🎮 Commands & Permissions

| Command | Description | Permission | Default |
| :--- | :--- | :--- | :--- |
| `/kaucjomat` | Opens the bottle recycling machine GUI | `kaucjomat.use` | Everyone / OP |
| `/kaucjomatdaj [amount]` | Spawns deposit bottles into your inventory | `kaucjomat.give` | OP only |
| `/kaucjomatreload` | Reloads the configuration file | `kaucjomat.reload` | OP only |

---

## ⚙️ Configuration

The configuration file is located at `plugins/localhost-kaucjomat/config.yml`:

```yaml
gui:
  title: "&8Kaucjomat"
  size: 27
  input-slots: [11, 12, 13, 14, 15]
  button-slot: 22
  button:
    material: EMERALD_BLOCK
    name: "&a&lWYMIEŃ BUTELKI"
    lore:
      - "&7Kliknij, aby sprzedać butelki."
      - "&7Otrzymasz: &e{MONEY}$ &7za każde &b{BOTTLES} sztuk&7!"
  background:
    material: GRAY_STAINED_GLASS_PANE
    name: " "

bottle:
  material: GLASS_BOTTLE
  name: "&aButelka Kaucyjna"
  lore:
    - "&7Wymień ją w kaucjomacie"
    - "&7Tylko do użytku ekologicznego!"

economy:
  bottles-required: 5   # Number of bottles required per reward set
  money-reward: 10.0    # Vault currency payout per set

messages:
  prefix: "&8[&aKaucjomat&8] &7"
  no-permission: "&cNie masz uprawnień!"
  reloaded: "&aPrzeładowano konfigurację!"
  given: "&aOtrzymałeś butelkę kaucyjną!"
  not-enough-bottles: "&cWłożyłeś za mało butelek! Minimum to {MIN}."
  success: "&aPomyślnie wymieniono &e{AMOUNT} &abutelek na &e{MONEY}$&a!"
  inventory-full: "&cMasz pełny ekwipunek, zwrócone butelki wypadły na ziemię!"
```

### Placeholders

| Placeholder | Context | Description |
| :--- | :--- | :--- |
| `{BOTTLES}` | `gui.button.lore` | Required number of bottles per set |
| `{MONEY}` | `gui.button.lore` / `messages.success` | Amount of money rewarded |
| `{MIN}` | `messages.not-enough-bottles` | Minimum required bottles to exchange |
| `{AMOUNT}` | `messages.success` | Number of bottles exchanged |

---

## 🛠️ Building from Source

This project uses Maven. To compile the plugin:

```bash
# Clone the repository
git clone https://github.com/your-username/Kaucjomat-plugin-minecraft.git

# Navigate to the project directory
cd localhost-kaucjomat

# Build the shaded JAR
mvn clean package
```

The compiled jar will be available in the `target/` directory:
```
target/localhost-kaucjomat-1.0-SNAPSHOT.jar
```

---

## 👤 Author & Support

- **Author**: Localhost
- **Discord**: `localhost_127001`
- **Issues**: Report bugs or feature requests via GitHub Issues
