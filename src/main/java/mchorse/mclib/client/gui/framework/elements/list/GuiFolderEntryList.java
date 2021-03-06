package mchorse.mclib.client.gui.framework.elements.list;

import java.util.List;
import java.util.function.Consumer;

import mchorse.mclib.client.gui.framework.GuiBase;
import mchorse.mclib.utils.files.entries.AbstractEntry;
import mchorse.mclib.utils.files.entries.FileEntry;
import mchorse.mclib.utils.files.entries.FolderEntry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

/**
 * Folder entry list GUI
 * 
 * This GUI list element allows to navigate through the file tree 
 * entries. 
 */
public class GuiFolderEntryList extends GuiListElement<AbstractEntry>
{
    public Consumer<FileEntry> fileCallback;
    public ResourceLocation rl;
    public FolderEntry parent;

    public GuiFolderEntryList(Minecraft mc, Consumer<FileEntry> fileCallback)
    {
        super(mc, null);

        this.callback = (entry) ->
        {
            if (entry instanceof FileEntry)
            {
                if (this.fileCallback != null)
                {
                    this.fileCallback.accept(((FileEntry) entry));
                }
            }
            else if (entry.isFolder())
            {
                this.setFolder((FolderEntry) entry);
            }
        };
        this.fileCallback = fileCallback;
        this.scroll.scrollItemSize = 16;
        this.scroll.scrollSpeed = 16;
    }

    /**
     * Set current folder
     */
    public void setFolder(FolderEntry folder)
    {
        /* Quick jump to children folder that has only one folder */
        if (folder.getEntries().size() <= 2 && !folder.isTop())
        {
            for (AbstractEntry subEntry : folder.getEntries())
            {
                if (subEntry.isFolder())
                {
                    FolderEntry subFolder = (FolderEntry) subEntry;

                    if (!subFolder.isTop())
                    {
                        this.setFolder(subFolder);

                        return;
                    }
                }
            }
        }

        this.setDirectFolder(folder);
    }

    public void setDirectFolder(FolderEntry folder)
    {
        List<AbstractEntry> entries = folder.getEntries();
        AbstractEntry current = this.getCurrent();

        this.parent = folder;
        this.setList(entries);
        this.current = current == null ? -1 : entries.indexOf(current);

        if (this.current == -1)
        {
            this.setCurrent(this.rl);
        }
    }

    public ResourceLocation getCurrentResource()
    {
        AbstractEntry entry = this.getCurrent();

        if (entry != null && entry instanceof FileEntry)
        {
            return ((FileEntry) entry).resource;
        }

        return null;
    }

    public void setCurrent(ResourceLocation rl)
    {
        this.current = -1;

        if (rl == null)
        {
            return;
        }

        for (int i = 0, c = this.list.size(); i < c; i++)
        {
            AbstractEntry entry = this.list.get(i);

            if (entry instanceof FileEntry && ((FileEntry) entry).resource.equals(rl))
            {
                this.current = i;
                break;
            }
        }
    }

    @Override
    public void sort()
    {}

    @Override
    public void drawElement(AbstractEntry element, int i, int x, int y, boolean hover)
    {
        if (this.current == i)
        {
            Gui.drawRect(x, y, x + this.scroll.w, y + this.scroll.scrollItemSize, 0x880088ff);
        }

        GlStateManager.enableAlpha();
        GlStateManager.enableBlend();
        this.mc.renderEngine.bindTexture(GuiBase.ICONS);

        GlStateManager.color(1, 1, 1, hover ? 0.8F : 0.6F);
        if (element instanceof FolderEntry)
        {
            Gui.drawScaledCustomSizeModalRect(x + 2, y, 112, 64, 16, 16, 16, 16, 256, 256);
        }
        else
        {
            Gui.drawScaledCustomSizeModalRect(x + 2, y, 96, 64, 16, 16, 16, 16, 256, 256);
        }

        this.font.drawStringWithShadow(element.title, x + 20, y + 4, hover ? 16777120 : 0xffffff);
    }
}