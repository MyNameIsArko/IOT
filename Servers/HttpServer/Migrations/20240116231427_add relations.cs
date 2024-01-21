using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

namespace HttpServer.Migrations
{
    /// <inheritdoc />
    public partial class addrelations : Migration
    {
        /// <inheritdoc />
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.RenameColumn(
                name: "DeviceId",
                table: "Devices",
                newName: "Id");

            migrationBuilder.CreateIndex(
                name: "IX_TopicDatas_DeviceId",
                table: "TopicDatas",
                column: "DeviceId");

            migrationBuilder.AddForeignKey(
                name: "FK_TopicDatas_Devices_DeviceId",
                table: "TopicDatas",
                column: "DeviceId",
                principalTable: "Devices",
                principalColumn: "Id",
                onDelete: ReferentialAction.Cascade);
        }

        /// <inheritdoc />
        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropForeignKey(
                name: "FK_TopicDatas_Devices_DeviceId",
                table: "TopicDatas");

            migrationBuilder.DropIndex(
                name: "IX_TopicDatas_DeviceId",
                table: "TopicDatas");

            migrationBuilder.RenameColumn(
                name: "Id",
                table: "Devices",
                newName: "DeviceId");
        }
    }
}
