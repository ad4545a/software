# AI Agent Responsibilities

| Agent | Responsibilities | Target Components |
| :--- | :--- | :--- |
| **Agent 1** | DB + Schema + Integrity | `model`, `db`, `repository` |
| **Agent 2** | Stock & Calculation Engine | `service/stock`, `model/Stock` |
| **Agent 3** | Billing Logic | `service/billing`, `model/Invoice` |
| **Agent 4** | Ledger & Accounts | `service/ledger`, `model/Account` |
| **Agent 5** | Reports | `reports`, `service/reporting` |
| **Agent 6** | Security & Licensing | `security`, `config`, `service/auth` |
| **Agent 7** | UI / JavaFX | `ui`, `controller`, `resources/css` |

## How to use
When assigning a task, identify which Agent role fits best and instruct the AI to assume that persona.
