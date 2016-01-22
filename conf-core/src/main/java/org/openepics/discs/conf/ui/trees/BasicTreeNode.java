package org.openepics.discs.conf.ui.trees;

import java.util.List;

import org.primefaces.model.TreeNode;

public abstract class BasicTreeNode<D> implements TreeNode {

	public static final String DEFAULT_TYPE = "default";
	private String type;
	private BasicTreeNode<D> parent;
	private D data;
	private boolean expanded;
    private boolean selected;    
    private boolean selectable = true;    
    private int rowKey;
	
	public BasicTreeNode(D data, BasicTreeNode<D> parent) {
        this.type = DEFAULT_TYPE;       
        this.data = data;        
        this.parent = parent;
    }
	
	public abstract List<? extends BasicTreeNode<D>> getAllChildren();
		
	@Override
	public String getType() {
		return type;
	}
	
	@Override
	public void setType(String type) {
		this.type = type;
	}
	
	@Override
	public D getData() {
		return data;
	}
	
	@Override
	public BasicTreeNode<D> getParent() {
		return parent;
	}
	
	@Override
	public void setParent(TreeNode parent) {
		//nothing
	}

	@Override
	public void clearParent() {
		//nothing		
	}

	
	@Override
	public boolean isExpanded() {
		return expanded;
	}

	@Override
	public void setExpanded(boolean expanded) {
		this.expanded = expanded;
	}

	@Override
    public boolean isSelected() {
        return this.selected;
    }

    @Override
    public void setSelected(boolean value) {
        this.selected = value;
    }

    @Override
    public boolean isSelectable() {
        return selectable;
    }

    @Override
    public void setSelectable(boolean selectable) {
        this.selectable = selectable;
    }
    
    @Override
	public int getChildCount() {
		return getChildren().size();
	}
	
	@Override
	public boolean isLeaf() {
		return getChildCount() == 0;
	}

	@Override
    public String getRowKey() {
		if (parent == null) return "root";
		else if (parent.parent == null) return String.valueOf(rowKey);
		else return parent.getRowKey()+"_"+String.valueOf(rowKey);
    }
	
	protected void updateRowKeys() {		
		int i = 0;
		for (TreeNode node : getChildren()) {
			((BasicTreeNode<D>)node).setRowKey(i);
			i++;
		}		 
	}

	@Override
	public void setRowKey(String rowKey) {
		
	}
	
    protected void setRowKey(int rowKey) {
        this.rowKey = rowKey;
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((data == null) ? 0 : data.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if(this == obj)
			return true;
		if(obj == null)
			return false;
		if(getClass() != obj.getClass())
			return false;
		
		BasicTreeNode<D> other = (BasicTreeNode<D>) obj;
		if (data == null) {
			if (other.data != null)
				return false;
		} else if (!data.equals(other.data))
			return false;
		
		return true;
	}

	@Override
	public String toString() {
		if(data != null)
			return data.toString();
		else
			return super.toString();
	}	

	@Override
    public boolean isPartialSelected() {
        return false;
    }

	@Override
    public void setPartialSelected(boolean value) {
        //nothing
    }
}
