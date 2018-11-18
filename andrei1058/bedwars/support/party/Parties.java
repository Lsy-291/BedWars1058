package com.andrei1058.bedwars.support.party;

import com.alessiodp.parties.api.interfaces.PartiesAPI;
import com.alessiodp.parties.api.interfaces.PartyPlayer;
import com.andrei1058.bedwars.configuration.language.Messages;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.andrei1058.bedwars.configuration.Language.getMsg;

public class Parties implements Party {

    //Support for Parties by AlessioDP
    PartiesAPI api = com.alessiodp.parties.api.Parties.getApi();

    @Override
    public boolean hasParty(Player p) {
        PartyPlayer pp = api.getPartyPlayer(p.getUniqueId());
        return pp == null ? true : api.getParty(pp.getPartyName()) != null ? true : false;
    }

    @Override
    public int partySize(Player p) {
        PartyPlayer pp = api.getPartyPlayer(p.getUniqueId());
        if (pp == null) return 0;
        com.alessiodp.parties.api.interfaces.Party party = api.getParty(pp.getPartyName());
        if (party == null) return 0;
        return party.getMembers().size();
    }

    @Override
    public boolean isOwner(Player p) {
        PartyPlayer pp = api.getPartyPlayer(p.getUniqueId());
        if (pp == null) return false;
        com.alessiodp.parties.api.interfaces.Party party = api.getParty(pp.getPartyName());
        if (party == null) return false;
        return party.getLeader() == p.getUniqueId();
    }

    @Override
    public List<Player> getMembers(Player p) {
        ArrayList<Player> players = new ArrayList<>();
        PartyPlayer pp = api.getPartyPlayer(p.getUniqueId());
        if (pp == null) return players;
        com.alessiodp.parties.api.interfaces.Party party = api.getParty(pp.getPartyName());
        if (party == null) return players;

        for (UUID pl : party.getMembers()) {
            Player on = Bukkit.getPlayer(pl);
            if (on == null) continue;
            if (!on.isOnline()) continue;
            players.add(on);
        }
        return players;
    }

    @Override
    public void createParty(Player owner, Player... members) {
    }

    @Override
    public void addMember(Player owner, Player member) {
    }

    @Override
    public void removeFromParty(Player member) {
        PartyPlayer pp = api.getPartyPlayer(member.getUniqueId());
        if (pp == null) return;
        com.alessiodp.parties.api.interfaces.Party party = api.getParty(pp.getPartyName());
        if (party == null) {
            api.removePlayerFromParty(pp);
        }
        if (party.getLeader() == member.getUniqueId()){
            disband(member);
        } else {
            api.removePlayerFromParty(pp);
            for (UUID mem : party.getMembers()) {
                Player p = Bukkit.getPlayer(mem);
                if (p == null) continue;
                if (!p.isOnline()) continue;
                p.sendMessage(getMsg(p, Messages.COMMAND_PARTY_LEAVE_SUCCESS).replace("{player}", member.getName()));
            }
        }
    }

    @Override
    public void disband(Player owner) {
        PartyPlayer pp = api.getPartyPlayer(owner.getUniqueId());
        if (pp == null) return;
        com.alessiodp.parties.api.interfaces.Party party = api.getParty(pp.getPartyName());
        if (party == null) return;
        for (UUID mem : party.getMembers()) {
            Player p = Bukkit.getPlayer(mem);
            if (p == null) continue;
            if (!p.isOnline()) continue;
            p.sendMessage(getMsg(p, Messages.COMMAND_PARTY_DISBAND_SUCCESS));
        }
        api.deleteParty(party);
    }

    @Override
    public boolean isMember(Player owner, Player check) {
        PartyPlayer pp = api.getPartyPlayer(owner.getUniqueId());
        if (pp == null) return false;
        com.alessiodp.parties.api.interfaces.Party party = api.getParty(pp.getPartyName());
        if (party == null) return false;
        return party.getMembers().contains(check.getUniqueId());
    }

    @Override
    public void removePlayer(Player owner, Player target) {
        PartyPlayer pp = api.getPartyPlayer(target.getUniqueId());
        if (pp == null) return;
        com.alessiodp.parties.api.interfaces.Party party = api.getParty(pp.getPartyName());
        if (party == null) {
            api.removePlayerFromParty(pp);
            return;
        }
        api.removePlayerFromParty(pp);
        for (UUID mem : party.getMembers()) {
            Player p = Bukkit.getPlayer(mem);
            if (p == null) continue;
            if (!p.isOnline()) continue;
            p.sendMessage(getMsg(p, Messages.COMMAND_PARTY_REMOVE_SUCCESS));
        }
    }

    @Override
    public boolean isInternal() {
        return false;
    }
}
