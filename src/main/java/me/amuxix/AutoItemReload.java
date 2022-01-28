package me.amuxix;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import static org.bukkit.event.EventPriority.MONITOR;
import static org.bukkit.inventory.EquipmentSlot.HAND;
import static org.bukkit.inventory.EquipmentSlot.OFF_HAND;

public class AutoItemReload extends JavaPlugin implements Listener {
    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this); //Registers the events on this file.
    }

    private void reloadHand(final Player player, final EquipmentSlot hand) {
        final PlayerInventory inventory = player.getInventory();
        final ItemStack heldItem = (hand == HAND ? inventory.getItemInMainHand() : inventory.getItemInOffHand()).clone();
        //Arrays.asList(inventory.getContents()).stream().map(itemStack -> itemStack.getData())
        new BukkitRunnable() {
            public void run() {
                int replacementSlot = inventory.first(heldItem.getType());
                if (replacementSlot != -1) {
                    // This is the item in hand after the event that triggered the reload has resolved
                    final ItemStack itemInHand = hand == HAND ? inventory.getItemInMainHand() : inventory.getItemInOffHand();
                    ItemStack replacement = inventory.getItem(replacementSlot);
                    inventory.setItem(hand, replacement);
                    inventory.setItem(replacementSlot, itemInHand);
                }
            }
        }.runTask(this);
    }

    @EventHandler(priority = MONITOR)
    public void onBlockPlaceEvent(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if (event.getItemInHand().getAmount() <= 1 && player.getGameMode() != GameMode.CREATIVE && !event.isCancelled()) {
            reloadHand(player, event.getHand());
        }
    }

    @EventHandler(priority = MONITOR)
    public void onPlayerBucketEmptyEvent(PlayerBucketEmptyEvent event) {
        Player player = event.getPlayer();
        if (player.getGameMode() != GameMode.CREATIVE && !event.isCancelled()) {
            reloadHand(player, event.getHand());
        }
    }

    @EventHandler(priority = MONITOR)
    public void onPlayerBucketFillEvent(PlayerBucketFillEvent event) {
        Player player = event.getPlayer();
        if (player.getGameMode() != GameMode.CREATIVE && !event.isCancelled()) {
            reloadHand(player, event.getHand());
        }
    }

    @EventHandler(priority = MONITOR)
    public void onPlayerItemBreakEvent(PlayerItemBreakEvent event) {
        Player player = event.getPlayer();
        if (player.getGameMode() != GameMode.CREATIVE) {
            reloadHand(player, HAND);
        }
    }

    @EventHandler
    public void onPlayerItemConsumeEvent(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();
        if (event.getItem().getAmount() <= 1 && player.getGameMode() != GameMode.CREATIVE && !event.isCancelled()) {
            if (player.getInventory().getItemInMainHand() == event.getItem()) {
                reloadHand(player, HAND);
            } else {
                reloadHand(player, OFF_HAND);
            }
        }
    }
    /*@EventHandler
    public void onPlayerEggThrowEvent(PlayerEggThrowEvent event) {
        getLogger().info("PlayerEggThrowEvent");
        Player player = event.getPlayer();
        PlayerInventory inventory = player.getInventory();
        int held_item_slot_index = inventory.getHeldItemSlot();
        int replacement_item_slot_index = getSimpleReplacementItemSlot(inventory, held_item_slot_index);
        if (replacement_item_slot_index != -1 && player.getItemInHand().getAmount() <= 1 && player.getGameMode() != GameMode.CREATIVE) {
            swapSlots(inventory, held_item_slot_index, replacement_item_slot_index);
        }
    }*/
}
