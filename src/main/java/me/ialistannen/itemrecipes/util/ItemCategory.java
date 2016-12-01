package me.ialistannen.itemrecipes.util;

import static com.perceivedev.perceivecore.reflection.ReflectionUtil.NameSpace.NMS;
import static com.perceivedev.perceivecore.reflection.ReflectionUtil.NameSpace.OBC;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.stream.Stream;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import com.perceivedev.perceivecore.reflection.ReflectionUtil;
import com.perceivedev.perceivecore.reflection.ReflectionUtil.MemberPredicate;
import com.perceivedev.perceivecore.reflection.ReflectionUtil.MethodPredicate;
import com.perceivedev.perceivecore.reflection.ReflectionUtil.Modifier;
import com.perceivedev.perceivecore.reflection.ReflectionUtil.ReflectResponse;
import com.perceivedev.perceivecore.util.ItemFactory;

/**
 * The Category of an item
 */
public enum ItemCategory {

    BUILDING_BLOCKS("buildingblocks", Material.BRICK),
    DECORATIONS("decorations", ItemFactory.builder(Material.DOUBLE_PLANT).setDurability((short) 5).build()),
    REDSTONE("redstone", Material.REDSTONE),
    TRANSPORTATION("transportation", Material.POWERED_RAIL),
    MISC("misc", Material.LAVA_BUCKET),
    FOOD("food", Material.APPLE),
    TOOLS("tools", Material.IRON_AXE),
    COMBAT("combat", Material.GOLD_SWORD),
    BREWING("brewing", Material.POTION),
    MATERIALS("materials", Material.STICK),
    UNKNOWN("unknown", Material.WEB);

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    private static final Class<?> ITEM_CLASS          = ReflectionUtil.getClass(NMS, "Item").get();
    @SuppressWarnings("OptionalGetWithoutIsPresent")
    private static final Class<?> BLOCK_CLASS         = ReflectionUtil.getClass(NMS, "Block").get();
    @SuppressWarnings("OptionalGetWithoutIsPresent")
    private static final Class<?> TAB_CLASS           = ReflectionUtil.getClass(NMS, "CreativeModeTab").get();
    @SuppressWarnings("OptionalGetWithoutIsPresent")
    private static final Class<?> CRAFT_MAGIC_NUMBERS = ReflectionUtil.getClass(OBC, "util.CraftMagicNumbers").get();
    private static final Method   GET_ITEM            = ReflectionUtil.getMethod(
              CRAFT_MAGIC_NUMBERS,
              new MethodPredicate().withName("getItem").withParameters(Material.class)
    ).getValue();
    private static final Method   GET_BLOCK           = ReflectionUtil.getMethod(
              CRAFT_MAGIC_NUMBERS,
              new MethodPredicate().withName("getBlock").withParameters(Material.class)
    ).getValue();

    private String    nmsName;
    private ItemStack icon;

    ItemCategory(String nmsName, Material icon) {
        this(nmsName, new ItemStack(icon));
    }

    ItemCategory(String nmsName, ItemStack icon) {
        this.nmsName = nmsName;
        this.icon = icon.clone();
    }

    /**
     * @return The NMS name
     */
    public String getNmsName() {
        return nmsName;
    }

    /**
     * @return The icon
     */
    public ItemStack getIcon() {
        return icon.clone();
    }

    /**
     * Returns the {@link ItemCategory} for an NMS creative tab
     *
     * @param nmsName The NMS name of the creative mode tab
     *
     * @return The ItemCategory. {@link #UNKNOWN} if not found
     */
    public static ItemCategory fromNMSName(String nmsName) {
        if (nmsName == null) {
            return UNKNOWN;
        }
        return Stream.of(values()).filter(itemCategory -> itemCategory.getNmsName().equalsIgnoreCase(nmsName)).findAny().orElse(UNKNOWN);
    }

    /**
     * Returns the {@link ItemCategory} for an NMS creative tab
     *
     * @param type The type to get it for
     *
     * @return The ItemCategory. {@link #UNKNOWN} if not found
     */
    public static ItemCategory fromMaterial(Material type) {
        ReflectResponse<Object> value;

        if (type.isBlock()) {
            value = ReflectionUtil.invokeMethod(GET_BLOCK, null, type);
            if (!value.isValuePresent()) {
                return UNKNOWN;
            }

            Object block = value.getValue();

            value = ReflectionUtil.getFieldValue(BLOCK_CLASS, block, new ReflectionUtil.FieldPredicate(TAB_CLASS));
        } else {
            value = ReflectionUtil.invokeMethod(GET_ITEM, null, type);
            if (!value.isValuePresent()) {
                return UNKNOWN;
            }

            Object item = value.getValue();

            value = ReflectionUtil.getFieldValue(ITEM_CLASS, item, new ReflectionUtil.FieldPredicate(TAB_CLASS));
        }
        if (!value.isValuePresent()) {
            return UNKNOWN;
        }

        return fromNMSName(getCreativeTabName(value));
    }

    /**
     * Returns the name of the field
     *
     * @param value The response of the CreativeTab field
     *
     * @return The Name of the tab
     */
    private static String getCreativeTabName(ReflectResponse<Object> value) {
        if (value.isValuePresent()) {
            Object tab = value.getValue();
            ReflectResponse<Object> name = ReflectionUtil.getFieldValue(TAB_CLASS, tab, new MemberPredicate<Field>()
                      .withModifiers(Modifier.FINAL, Modifier.PRIVATE).and(field -> field.getType() == String.class));
            if (name.isValuePresent()) {
                return (String) name.getValue();
            }
        }
        return null;
    }
}
