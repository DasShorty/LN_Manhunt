package com.laudynetwork.manhunt.game.running;

import com.laudynetwork.manhunt.Manhunt;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

public class RunningTimer {

    int sec, min, hour = 0;

    @Getter
    private final BukkitTask task;

    public RunningTimer() {
        task = run();
    }

    public String getMinutes() {
        return toString(min);
    }

    public String getSeconds() {
        return toString(sec);
    }

    public String getHours() {
        return toString(hour);
    }

    private String toString(int i) {
        if (i < 10)
            return "0" + i;
        return String.valueOf(i);
    }

    private void increase() {

        sec++;
        if (sec == 60) {
            sec = 0;
            min++;
        }

        if (min == 60) {
            min = 0;
            hour++;
        }

    }

    private BukkitTask run() {
        return Bukkit.getScheduler().runTaskTimerAsynchronously(Manhunt.getINSTANCE(), this::increase, 0, 20);
    }
}
