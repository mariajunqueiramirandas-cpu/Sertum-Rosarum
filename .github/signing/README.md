# Stable fallback signer

`fallback.keystore` is an intentionally public **development** signing identity. It is used for local debug builds and optimized release APKs when private signing credentials are absent. This fixed identity lets automatic builds update one another across different runners.

- Store password: `android`
- Alias: `androiddebugkey`
- Key password: `android`
- Public certificate: [`fallback-certificate.pem`](fallback-certificate.pem)

Keep this file unchanged to preserve update compatibility. Because the fallback key is public, use private signing credentials for production distribution.

Private signing is optional. Add all four repository Actions secrets to override the fallback on `main`:

| Secret | Value |
| --- | --- |
| `ANDROID_KEYSTORE_BASE64` | Base64-encoded private keystore |
| `ANDROID_KEYSTORE_PASSWORD` | Keystore password |
| `ANDROID_KEY_ALIAS` | Signing alias |
| `ANDROID_KEY_PASSWORD` | Signing key password |

The workflow decodes a private keystore only into the runner's temporary directory. Private credentials are not provided to pull-request builds. A partial set of credentials fails instead of silently changing the signing identity.

For a private local build, use `ANDROID_KEYSTORE_FILE` and the three password/alias environment variables. Changing an already-installed app's signing certificate requires uninstalling that copy before switching identities.
