package isaac.bastion;

import isaac.bastion.util.QTBox;
import isaac.bastion.util.SparseQuadTree;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.block.BlockPlaceEvent;

import com.untamedears.citadel.entity.Faction;


public class BastionManager
{
	//private Vector<BastionBlock> bastions;
	private Map<World,SparseQuadTree> bastions;
	private List<World> worlds;
	public BastionManager()
	{
		Bastion.getPlugin();
		worlds=Bukkit.getWorlds();
		bastions=new HashMap<World, SparseQuadTree>();
		for(World world : worlds){
			SparseQuadTree bastionsForWorld=new SparseQuadTree();
			bastions.put(world, bastionsForWorld);
		}
		//bastions = new Vector<BastionBlock>();
	}

	public void addBastion(Location location, int strength, Faction creator) {
		//bastions.add(new BastionBlock(location, strength, creator));
		bastions.get(location.getWorld()).add(new BastionBlock(location, strength, creator));
		Bastion.getPlugin().getLogger().info("bastion added");
	}
	public BastionBlock getBastionBlock(Location loc) {
		Set<? extends QTBox> possible=bastions.get(loc.getWorld()).find(loc.getBlockX(), loc.getBlockZ());
		for(QTBox box: possible){
			BastionBlock bastion=(BastionBlock) box;
			if(bastion.getLocation()==loc)
				return bastion;
		}
		return null;
	}
	public void removeBastion(Location location) {
		bastions.get(location.getWorld()).remove(getBastionBlock(location));
	}
	
	public void handleBlockPlace(BlockPlaceEvent event) {
		Location location=event.getBlock().getLocation();
		Set<? extends QTBox> possible=bastions.get(event.getBlock().getLocation().getWorld()).find(location.getBlockX(), location.getBlockZ());
		@SuppressWarnings("unchecked")
		List<BastionBlock> possibleRandom=new LinkedList<BastionBlock>((Set<BastionBlock>)possible);
		Collections.shuffle(possibleRandom);
		for (BastionBlock bastion : possibleRandom){
			if (bastion.blocked(event)){
				bastion.handlePlaced(event.getBlock());
		        if(bastion.shouldCull())
		        	bastions.get(bastion.getLocation().getWorld()).remove(bastion);
		        break;
			}
		}
	}
}