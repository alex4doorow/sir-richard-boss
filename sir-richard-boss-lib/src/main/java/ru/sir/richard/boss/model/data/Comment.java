package ru.sir.richard.boss.model.data;

import ru.sir.richard.boss.model.types.CommentTypes;

public class Comment extends AnyId {
	
	private CommentTypes commentType;
	private String key;
	private String value;
	
	public Comment(int id, CommentTypes commentType, String key, String value) {
		super(id);
		this.commentType = commentType;
		this.key = key;
		this.value = value;
	}	
	
	public Comment(CommentTypes commentType) {
		super();
		this.commentType = commentType;
	}	
	
	public Comment(CommentTypes commentType, String key, String value) {
		this(commentType);
		this.key = key;
		this.value = value;
	}	

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public CommentTypes getCommentType() {
		return commentType;
	}		
	
	@Override
	public Comment clone() throws CloneNotSupportedException  {
		Comment clone = (Comment) super.clone();		
		clone.commentType = this.commentType;
		clone.key = this.key == null ? null : new String(this.key);
		clone.value = this.value == null ? null : new String(this.value);		
		return clone;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((commentType == null) ? 0 : commentType.hashCode());
		result = prime * result + ((key == null) ? 0 : key.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		Comment other = (Comment) obj;
		if (commentType != other.commentType)
			return false;
		if (key == null) {
			if (other.key != null)
				return false;
		} else if (!key.equals(other.key))
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "OrderComment [commentType=" + commentType + ", key=" + key + ", value=" + value + "]";
	}

}
