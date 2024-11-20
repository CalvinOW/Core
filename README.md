# Core Plugin

Core is a custom plugin created for Minecraft servers, offering various utilities and features designed to enhance the gameplay experience. It is designed with flexibility and ease of use in mind, making it a great addition to any Minecraft server.

## Features

- **Command Help**: Shows useful commands for users.
- **Player Reporting**: Allows players to report others directly to staff via Discord.
- **Customizable Prefix**: Easily configurable prefix for the plugin’s messages.
  
## Installation

1. Download the latest version of the plugin.
2. Place the `.jar` file in your Minecraft server’s `plugins` folder.
3. Restart or reload the server to enable the plugin.
4. Configure the plugin by modifying the `config.yml` and `messages.yml` files to your liking.

## Configuration

You can modify the plugin's settings in the `config.yml` file. Make sure to set up your Discord webhook URL for player reports.

### Example `config.yml`:

```yaml
prefix: "&8[&fCore&8] &7»&f"
discord_webhook_url: "YOUR_DISCORD_WEBHOOK_URL"
