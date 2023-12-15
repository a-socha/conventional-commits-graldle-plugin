# Conventional Commits plugin

Gradle plugin to support https://www.conventionalcommits.org/en/v1.0.0/ in java ecosystem.

## Tasks

### newVersion

```shell
gradle newVersion
```

Task is scanning repository and bump up version according to Conventional Commits.
Task is pushing new tag to repository by default.

#### Options

* `--no-push` - add if you would like to create new tag but not push to repo yet

### printNewVersion

```shell
gradle printNewVersion
```

Task is scanning repository and calculates new version according to Conventional Commits.
New version is only printed on standard output.

## Configuration

There is a possibility to override default Conventional Commit settings, by providing own types of commits or changing
behaviour of existing ones.

### Default config

```groovy
conventionalCommitsConfig {
    major = []
    minor = ["feat"]
    patch = ["fix", "chore", "style", "refactor", "perf", "test", "ci", "build"]
    none = ["docs"]
}
```