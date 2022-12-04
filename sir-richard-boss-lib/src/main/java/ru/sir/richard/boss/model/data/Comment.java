package ru.sir.richard.boss.model.data;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ru.sir.richard.boss.model.types.CommentTypes;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Comment extends AnyId {
	
	private CommentTypes commentType;
	private String key;
	private String value;

	public Comment(CommentTypes commentType) {
		super();
		this.commentType = commentType;
	}

	public Comment(int id, CommentTypes commentType, String key, String value) {
		super(id);
		this.commentType = commentType;
		this.key = key;
		this.value = value;
	}

	@Override
	public Comment clone() throws CloneNotSupportedException  {
		Comment clone = (Comment) super.clone();		
		clone.commentType = this.commentType;
		clone.key = (this.key == null) ? null : this.key;
		clone.value = (this.value == null) ? null : this.value;
		return clone;
	}
}
