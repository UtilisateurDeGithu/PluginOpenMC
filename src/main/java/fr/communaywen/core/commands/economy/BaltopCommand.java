package fr.communaywen.core.commands.economy;

import fr.communaywen.core.AywenCraftPlugin;
import fr.communaywen.core.credit.Credit;
import fr.communaywen.core.credit.Feature;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Description;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.util.*;

@Feature("baltop")
@Credit("ddemile")
public class BaltopCommand {
    private static AywenCraftPlugin plugin;

    public BaltopCommand(AywenCraftPlugin plugin) {
        this.plugin = plugin;
    }

    @Command({"baltop", "balancetop"})
    @Description("Affiche la liste des joueurs les plus riches du serveur")
    public void onCommand(Player player) {
        List<PlayerBalance> balances = this.getBalances();

        balances.sort((a, b) -> a.balance.intValue() - b.balance.intValue());

        balances = balances.reversed();

        if (balances.size() > 20) {
            balances = balances.subList(0, 20);
        }

        List<String> lines = new ArrayList<>();
        lines.add(ChatColor.DARK_GREEN + "Liste des plus grandes fortunes du serveur:");

        int index = 1;
        for (PlayerBalance playerBalance : balances) {
            String playerName = playerBalance.playerId.toString();
            if (Bukkit.getPlayer(playerBalance.playerId) != null) {
                playerName = Bukkit.getOfflinePlayer(playerBalance.playerId).getName();
            } else if (Bukkit.getOfflinePlayer(playerBalance.playerId) != null) {
                playerName = Bukkit.getOfflinePlayer(playerBalance.playerId).getName();
            }
            lines.add(MessageFormat.format("{0}. {1}: {2}", getColor(index) + index, ChatColor.GRAY + playerName, ChatColor.GREEN + playerBalance.getFormattedBalance()));

            index++;
        }

        player.sendMessage(String.join("\n", lines));
    }

    public static String getColor(int index) {
        return (switch (index) {
            case 1 -> ChatColor.GOLD;
            case 2 -> ChatColor.of("#d7d7d7");
            case 3 -> ChatColor.of("#945604");
            default -> ChatColor.WHITE;
        }).toString();
    }

    public static List<PlayerBalance> getBalances() {
        List<PlayerBalance> balances = new ArrayList<>();

        plugin.getManagers().getEconomyManager().getBalances().forEach((UUID uuid, Double balance) -> {
            balances.add(
                    new PlayerBalance(uuid, balance)
            );
        });

        return balances;
    }

    public static class PlayerBalance {
        public UUID playerId;
        public Double balance;

        public PlayerBalance(UUID playerId, Double balance) {
            this.playerId = playerId;
            this.balance = balance;
        }

        public String getFormattedBalance() {
            String balance = String.valueOf(this.balance);
            Currency currency = Currency.getInstance("EUR");
            NumberFormat formatter = NumberFormat.getCurrencyInstance(Locale.UK);
            formatter.setCurrency(currency);
            BigDecimal bd = new BigDecimal(balance);
            return formatter.format(bd);
        }
    }
}
