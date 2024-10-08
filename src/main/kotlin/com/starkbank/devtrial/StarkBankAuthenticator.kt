package com.starkbank.devtrial

import com.azure.identity.DefaultAzureCredentialBuilder
import com.azure.security.keyvault.secrets.SecretClientBuilder
import com.starkbank.Project
import com.starkbank.Settings

object StarkBankAuthenticator {
    fun authenticate() {
        val secretKey = if (System.getenv("IS_RUNNING_LOCALLY").toBoolean()) {
            getPrivateKey()
        } else {
            System.getenv("STARK_BANK_API_SECRET_KEY")
        }

        val user = Project(
            Constants.ENV,
            Constants.PROJECT_ID,
            secretKey
        )

        Settings.user = user
    }

    private fun getPrivateKey(): String {
        val keyVaultName = "dev-starkbank-kv"
        val secretName = "dev-starkbank-api-private-key"
        val keyVaultUrl = "https://$keyVaultName.vault.azure.net"

        val client = SecretClientBuilder()
            .vaultUrl(keyVaultUrl)
            .credential(DefaultAzureCredentialBuilder().build())
            .buildClient()

        val key = client.getSecret(secretName).value

        val strBuilder = StringBuilder()

        key
            .split("\\n")
            .forEach { strBuilder.appendLine(it) }

        return strBuilder.toString()
    }
}