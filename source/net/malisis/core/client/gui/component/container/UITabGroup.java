/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Ordinastie
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package net.malisis.core.client.gui.component.container;

import java.util.LinkedHashMap;
import java.util.Map;

import net.malisis.core.client.gui.MalisisGui;
import net.malisis.core.client.gui.component.interaction.UITab;
import net.malisis.core.client.gui.event.ComponentEvent;
import net.malisis.core.client.gui.icon.GuiIcon;

/**
 * @author Ordinastie
 *
 */
public class UITabGroup extends UIContainer<UITabGroup>
{
	public enum Position
	{
		TOP, RIGHT, LEFT, BOTTOM
	}

	protected Map<UITab, UIContainer> listTabs = new LinkedHashMap<>();
	protected UITab activeTab;
	protected Position tabPosition = Position.TOP;
	protected UIContainer attachedContainer;
	protected int offset = 3;
	protected GuiIcon[] windowIcons;
	protected GuiIcon[] panelIcons;

	public UITabGroup(MalisisGui gui, Position tabPosition)
	{
		super(gui);
		this.tabPosition = tabPosition;
		clipContent = false;
		setSize(0, 0);

		//@formatter:off
		windowIcons = new GuiIcon[] {	gui.getGuiTexture().getXYResizableIcon(0, 60, 15, 15, 5),
										gui.getGuiTexture().getXYResizableIcon(15, 60, 15, 15, 5),
										gui.getGuiTexture().getXYResizableIcon(0, 75, 15, 15, 5),
										gui.getGuiTexture().getXYResizableIcon(15, 75, 15, 15, 5)};

		panelIcons = new GuiIcon[] {	gui.getGuiTexture().getXYResizableIcon(30, 60, 15, 15, 5),
										gui.getGuiTexture().getXYResizableIcon(45, 60, 15, 15, 5),
										gui.getGuiTexture().getXYResizableIcon(30, 75, 15, 15, 5),
										gui.getGuiTexture().getXYResizableIcon(45, 75, 15, 15, 5)};
		//@formatter:on
	}

	public UITabGroup(MalisisGui gui)
	{
		this(gui, Position.TOP);
	}

	/**
	 * @return the relative position of the tabs around their containers.
	 */
	public Position getTabPosition()
	{
		return tabPosition;
	}

	public GuiIcon getIcons()
	{
		if (attachedContainer == null)
			return null;

		if (attachedContainer instanceof UIPanel)
			return panelIcons[tabPosition.ordinal()];
		else
			return windowIcons[tabPosition.ordinal()];
	}

	/**
	 * Adds tab and its corresponding container to this <code>UITabGroup</code>.<br />
	 * Also sets the width of this <code>UITabGroup</code>.
	 *
	 * @param tab
	 * @param container
	 */
	public UITab addTab(UITab tab, UIContainer container)
	{
		add(tab);
		tab.setContainer(container);
		tab.setActive(false);
		listTabs.put(tab, container);

		calculateTabPosition();

		return tab;
	}

	public void calculateTabPosition()
	{
		int w = 0;
		int h = 0;

		for (UITab tab : listTabs.keySet())
		{
			if (tabPosition == Position.TOP || tabPosition == Position.BOTTOM)
			{
				tab.setPosition(w + offset, 1);
				w += tab.getWidth();
				h = Math.max(h, tab.getHeight());
			}
			else
			{
				tab.setPosition(1, h + offset);
				w = Math.max(w, tab.getWidth());
				h += tab.getHeight();
			}
		}

		boolean isHorizontal = tabPosition == Position.TOP || tabPosition == Position.BOTTOM;
		for (UITab tab : listTabs.keySet())
			tab.setSize(isHorizontal ? 0 : w, isHorizontal ? h : 0);

		if (isHorizontal)
			w += offset * 2;
		else
			h += offset * 2;

		setSize(w + 2, h + 2);
	}

	/**
	 * Activates the tab and deactivates currently active tab.
	 *
	 * @param tab
	 */
	public void setActiveTab(UITab tab)
	{
		if (activeTab == tab)
			return;

		if (activeTab != null)
			activeTab.setActive(false);

		activeTab = tab;
		if (tab != null)
			tab.setActive(true);

		if (attachedContainer != null)
			attachedContainer.setBackgroundColor(tab.getColor());

	}

	public UIContainer getAttachedContainer()
	{
		return attachedContainer;
	}

	/**
	 * Attach the container to this <code>UITabGroup</code>.
	 *
	 * @param container
	 * @param resize
	 * @return
	 */
	public UITabGroup attachTo(UIContainer container, boolean displace)
	{
		attachedContainer = container;
		setZIndex(attachedContainer.getZIndex() - 1);
		if (activeTab != null)
			attachedContainer.setBackgroundColor(activeTab.getColor());

		if (!displace)
			return this;

		int cx = container.getX();
		int cy = container.getY();
		int cw = container.getBaseWidth();
		int ch = container.getBaseHeight();

		if (tabPosition == Position.TOP)
		{
			cy += getHeight() - 1;
			ch = (container.getBaseHeight() == INHERITED ? 0 : container.getBaseHeight()) - getHeight();
		}
		else if (tabPosition == Position.BOTTOM)
		{
			ch = (container.getBaseHeight() == INHERITED ? 0 : container.getBaseHeight()) - getHeight() + 1;
		}
		else if (tabPosition == Position.LEFT)
		{
			cx += getWidth() - 1;
			cw = (container.getBaseWidth() == INHERITED ? 0 : container.getBaseWidth()) - getWidth();
		}
		else if (tabPosition == Position.RIGHT)
		{
			cw = (container.getBaseWidth() == INHERITED ? 0 : container.getBaseWidth()) - getWidth() + 1;
		}

		//tab.setSize(w, h);
		container.setSize(cw, ch);
		container.setPosition(cx, cy);
		return this;
	}

	@Override
	public int screenX()
	{
		if (attachedContainer == null)
			return super.screenX();

		int x = this.x + attachedContainer.screenX();
		switch (tabPosition)
		{
			case LEFT:
				x += offset - getWidth();
				break;
			case RIGHT:
				x += attachedContainer.getWidth() - offset;
				break;
			default:
				break;

		}

		return x;
	}

	@Override
	public int screenY()
	{
		if (attachedContainer == null)
			return super.screenY();

		int y = this.y + attachedContainer.screenY();
		switch (tabPosition)
		{
			case TOP:
				y += offset - getHeight();
				break;
			case BOTTOM:
				y += attachedContainer.getHeight() - offset;
				break;
			default:
				break;

		}
		return y;
	}

	/**
	 * Event fired when an inactive {@link UITab} is clicked.<br>
	 * Canceling the event will keep the old tab active.
	 *
	 * @author Ordinastie
	 *
	 */
	public static class TabChangeEvent extends ComponentEvent<UITabGroup>
	{
		private UITab newTab;

		public TabChangeEvent(UITabGroup component, UITab newTab)
		{
			super(component);
			this.newTab = newTab;
		}

		/**
		 * @return the {@link UITab} deactivated
		 */
		public UITab getOldTab()
		{
			return component.activeTab;
		}

		/**
		 * @return the {@link UITab} activated
		 */
		public UITab getNewTab()
		{
			return newTab;
		}

	}
}
