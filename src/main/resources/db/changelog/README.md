# Database Changelog Structure
Some rules to follow while contributing to the changelogs:
- `000_genesis` — schema baseline
- Feature folders — append-only schema evolution
- One concern per file
- Never modify applied changesets

# Changeset ID Naming
Use the following structure:
```text
<order>-<action>-<object>
```

Example:
```text
001-create-event-table
```
WHERE

`order` is `001`

`action` is `create`

`object` is `event-table`

# AUTHOR
Always set author to `SYSTEM`.

