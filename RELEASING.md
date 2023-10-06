# Release process

Release process is semi-automated through GitHub Actions. This describes the basic steps for a project member to perform a release.

## Steps

1. Ensure that the `main` branch is building and that tests are passing.
1. Create a new release on GitHub.
1. The release triggers a GitHub Action workflow.
1. Handcraft and polish some of the release notes (e.g. substitute combined dependency PRs and highlight certain features).
1. Rename existing milestone corresponding to new release and close it. Then create a new `next` milestone.

## Internal details

* JReleaser is in charge of steps in Maven Central.
