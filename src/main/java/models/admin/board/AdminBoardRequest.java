package models.admin.board;

import javax.validation.constraints.Min;

import org.hibernate.validator.constraints.NotBlank;

public class AdminBoardRequest {

	@NotBlank(message = "게시판 아이디를 입력해 주세요.")
	private String boardId;
	@NotBlank(message = "게시판 이름을 입력해 주세요.")
	private String boardName;
	@Min(value = 1, message = "게시판에 표시할 개수를 정해주세요.")
	private int pageCount;
	private int isUse;
	private int memberOnly;
	private int commentUse;

	public String getBoardId() {
		return boardId;
	}

	public void setBoardId(String boardId) {
		this.boardId = boardId;
	}

	public String getBoardName() {
		return boardName;
	}

	public void setBoardName(String boardName) {
		this.boardName = boardName;
	}

	public int getPageCount() {
		return pageCount;
	}

	public void setPageCount(int pageCount) {
		this.pageCount = pageCount;
	}

	public int getIsUse() {
		return isUse;
	}

	public void setIsUse(int isUse) {
		this.isUse = isUse;
	}

	public int getMemberOnly() {
		return memberOnly;
	}

	public void setMemberOnly(int memberOnly) {
		this.memberOnly = memberOnly;
	}

	public int getCommentUse() {
		return commentUse;
	}

	public void setCommentUse(int commentUse) {
		this.commentUse = commentUse;
	}

}
