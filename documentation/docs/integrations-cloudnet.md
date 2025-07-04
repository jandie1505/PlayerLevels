# CloudNet Integration
The CloudNet Integration automatically sets the PlayerLevels `server_id` to the current task name.

## v1.2+
Since version 1.2, CloudNet support is directly integrated into PlayerLevels-Core.
Unlike before, you don't need to install additional plugins for it.

You can enable the CloudNet integration in the `config.yml`:
```yml
# [...]
integrations:
  cloudnet: true
```
(It should be enabled by default when CloudNet-Bridge is detected).

## Older versions
For versions before 1.2, an extra plugin named `PlayerLevels-CloudNet` is required.

### Installation
1. Download the PlayerLevels-CloudNet plugin.
2. Put the Plugin into the same template as your PlayerLevels plugin.
3. Restart the servers which are using the template.

### Usage
The plugin does everything for you, just install it, and it will work.

### Recommended setup
1. Create a new PlayerLevels template
2. Put PlayerLevels (and its config files) and PlayerLevels-CloudNet into its `plugins` directory.
3. Add the template to all tasks that should have PlayerLevels available.
